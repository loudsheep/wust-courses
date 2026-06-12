import csv
import re
from datetime import datetime
from pathlib import Path
from typing import Optional, TypedDict

from timeseries import TimeSeries
from detectors import (
    OutlierDetector,
    ZeroSpikeDetector,
    ThresholdDetector,
    SimpleReporter,
    SeriesValidator,
)


class FileMeta(TypedDict):
    path: Path
    year: int
    filename_indicator: str
    filename_avg: str
    stations: list[str]
    indicators: list[str]
    averaging_times: list[str]
    units: list[str]
    station_col_map: dict[str, int]


class CacheEntry(TypedDict):
    full: Optional[list[TimeSeries]]
    stations: dict[str, TimeSeries]
    parameters: dict[str, list[TimeSeries]]


class Measurements:
    def __init__(self, data_dir: str | Path):
        self.data_dir = Path(data_dir)
        if not self.data_dir.exists():
            raise ValueError(f"Katalog nie istnieje: {self.data_dir}")

        self.file_pattern = re.compile(r"^(\d{4})_(.+)_([a-zA-Z0-9]+)\.csv$")

        self.file_metadata: list[FileMeta] = []
        self._scan_files()

        self._cache: dict[int, CacheEntry] = {}

    def _scan_files(self) -> None:
        for file_path in self.data_dir.glob("*.csv"):
            match = self.file_pattern.match(file_path.name)
            if not match:
                continue
            year, filename_indicator, filename_avg = match.groups()

            try:
                with open(file_path, "r", newline="", encoding="utf-8") as f:
                    reader = csv.reader(f)
                    header_rows = []
                    for _ in range(6):
                        try:
                            header_rows.append(next(reader))
                        except StopIteration:
                            break
            except Exception:
                continue

            if len(header_rows) < 2:
                continue

            code_row = header_rows[1]
            indicator_row = header_rows[2] if len(header_rows) > 2 else []
            averaging_row = header_rows[3] if len(header_rows) > 3 else []
            unit_row = header_rows[4] if len(header_rows) > 4 else []

            stations: list[str] = []
            indicators: list[str] = []
            averaging_times: list[str] = []
            units: list[str] = []
            station_col_map: dict[str, int] = {}

            for idx in range(1, len(code_row)):
                code = code_row[idx].strip()

                if not code:
                    continue

                stations.append(code)
                station_col_map[code] = idx

                indicators.append(
                    indicator_row[idx].strip() if idx < len(indicator_row) else ""
                )
                averaging_times.append(
                    averaging_row[idx].strip() if idx < len(averaging_row) else ""
                )
                units.append(unit_row[idx].strip() if idx < len(unit_row) else "")

            self.file_metadata.append(
                {
                    "path": file_path,
                    "year": int(year),
                    "filename_indicator": filename_indicator,
                    "filename_avg": filename_avg,
                    "stations": stations,
                    "indicators": indicators,
                    "averaging_times": averaging_times,
                    "units": units,
                    "station_col_map": station_col_map,
                }
            )

    def _parse_csv_columns(
        self, file_path: Path, col_indices: list[int]
    ) -> list[TimeSeries]:
        try:
            with open(file_path, "r", newline="", encoding="utf-8") as f:
                reader = csv.reader(f)
                header = []
                for _ in range(6):
                    try:
                        header.append(next(reader))
                    except StopIteration:
                        break

                col_to_values: dict[int, list[Optional[float]]] = {
                    ci: [] for ci in col_indices
                }
                datetimes: list[datetime] = []

                for row in reader:
                    if not row:
                        continue
                    ts_str = row[0].strip()
                    if not ts_str:
                        continue

                    try:
                        dt = datetime.strptime(ts_str, "%d/%m/%y %H:%M")
                    except ValueError:
                        continue
                    datetimes.append(dt)

                    for ci in col_indices:
                        val = None
                        if ci < len(row):
                            s = row[ci].strip()
                            if s:
                                try:
                                    val = float(s.replace(",", "."))
                                except ValueError:
                                    val = None
                        col_to_values[ci].append(val)

        except Exception as e:
            raise ValueError(f"Nie można odczytać pliku {file_path}: {e}")

        series_list: list[TimeSeries] = []

        code_row = header[1] if len(header) > 1 else []
        indicator_row = header[2] if len(header) > 2 else []
        averaging_row = header[3] if len(header) > 3 else []
        unit_row = header[4] if len(header) > 4 else []

        for ci in col_indices:
            station_code = code_row[ci].strip() if ci < len(code_row) else ""
            indicator = indicator_row[ci].strip() if ci < len(indicator_row) else ""
            averaging = averaging_row[ci].strip() if ci < len(averaging_row) else ""
            unit = unit_row[ci].strip() if ci < len(unit_row) else None

            values = col_to_values.get(ci, [])

            if datetimes and values:
                ts = TimeSeries(
                    indicator=indicator,
                    station_code=station_code,
                    averaging_time=averaging,
                    datetimes=list(datetimes),
                    values=list(values),
                    unit=unit if unit else None,
                )
                series_list.append(ts)

        return series_list

    def _load_file_data(
        self,
        file_idx: int,
        station_code: Optional[str] = None,
        parameter: Optional[str] = None,
    ) -> list[TimeSeries]:
        if file_idx not in self._cache:
            self._cache[file_idx] = {"full": None, "stations": {}, "parameters": {}}

        cache_entry = self._cache[file_idx]
        meta = self.file_metadata[file_idx]
        path: Path = meta["path"]

        if station_code:
            if station_code in cache_entry["stations"]:
                return [cache_entry["stations"][station_code]]

            col_map = meta["station_col_map"]
            if station_code not in col_map:
                return []

            col_idx = col_map[station_code]
            series_list = self._parse_csv_columns(path, [col_idx])

            if series_list:
                cache_entry["stations"][station_code] = series_list[0]
                return [series_list[0]]

            return []

        if parameter:
            if parameter in cache_entry["parameters"]:
                return cache_entry["parameters"][parameter]

            cols: list[int] = []
            for idx, ind in enumerate(meta["indicators"]):
                if ind == parameter:
                    station_code = meta["stations"][idx]
                    cols.append(meta["station_col_map"][station_code])

            if not cols:
                cache_entry["parameters"][parameter] = []
                return []

            series_list = self._parse_csv_columns(path, cols)
            cache_entry["parameters"][parameter] = series_list
            return series_list

        if cache_entry["full"] is not None:
            return cache_entry["full"]

        cols = list(meta["station_col_map"].values())
        series_list = self._parse_csv_columns(path, cols)
        cache_entry["full"] = series_list
        return series_list

    def __len__(self) -> int:
        return sum(len(meta["stations"]) for meta in self.file_metadata)

    def __contains__(self, parameter_name: str) -> bool:
        for meta in self.file_metadata:
            if any(ind == parameter_name for ind in meta["indicators"]):
                return True

        return False

    def get_by_parameter(self, param_name: str) -> list[TimeSeries]:
        result: list[TimeSeries] = []
        for file_idx in range(len(self.file_metadata)):
            meta = self.file_metadata[file_idx]

            if any(ind == param_name for ind in meta["indicators"]):
                series = self._load_file_data(file_idx, parameter=param_name)
                result.extend(series)

        return result

    def get_by_station(self, station_code: str) -> list[TimeSeries]:
        result: list[TimeSeries] = []
        for file_idx in range(len(self.file_metadata)):
            meta = self.file_metadata[file_idx]

            if station_code in meta["station_col_map"]:
                series = self._load_file_data(file_idx, station_code=station_code)
                result.extend(series)

        return result

    def detect_all_anomalies(
        self, validators: list[SeriesValidator], preload: bool = False
    ) -> dict[str, list[str]]:
        results: dict[str, list[str]] = {}

        series_to_validate: list[TimeSeries] = []

        if preload:
            for file_idx in range(len(self.file_metadata)):
                loaded_series = self._load_file_data(file_idx)
                series_to_validate.extend(loaded_series)
        else:
            for cache_entry in self._cache.values():
                if cache_entry["full"] is not None:
                    series_to_validate.extend(cache_entry["full"])
                else:
                    series_to_validate.extend(cache_entry["stations"].values())
                    for series_list in cache_entry["parameters"].values():
                        series_to_validate.extend(series_list)

        unique_series: list[TimeSeries] = []
        seen_ids: set[int] = set()

        for series in series_to_validate:
            sid = id(series)

            if sid not in seen_ids:
                seen_ids.add(sid)
                unique_series.append(series)

        anomalies: list[str] = []

        for series in unique_series:
            series_key = (
                f"{series.station_code} | {series.indicator} | {series.averaging_time}"
            )

            for validator in validators:
                anomalies.extend(validator.analyze(series))

            if anomalies:
                results[series_key] = anomalies

        return results


def example() -> None:
    ts = TimeSeries(
        indicator="PM10",
        station_code="TEST",
        averaging_time="1h",
        datetimes=[datetime.now()],
        values=[10.0],
        unit="ug/m3",
    )

    analyzers: list[SeriesValidator] = [
        OutlierDetector(k=3),
        ZeroSpikeDetector(),
        ThresholdDetector(threshold=25),
        SimpleReporter(),  # type: ignore
    ]

    for a in analyzers:
        print(a.analyze(ts))


def main() -> None:
    m = Measurements("../lista5/data/measurements")
    validators: list[SeriesValidator] = [
        OutlierDetector(k=3),
        ZeroSpikeDetector(),
        ThresholdDetector(threshold=25),
        SimpleReporter(),  # type: ignore
    ]
    print(m.detect_all_anomalies(validators, preload=False))
    m.get_by_station(station_code="DsWrocWybCon")
    print(m.detect_all_anomalies(validators, preload=False))
    print(m.detect_all_anomalies(validators, preload=True))


if __name__ == "__main__":
    main()
