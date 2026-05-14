from datetime import date, datetime
from typing import Any


class TimeSeries:
    def __init__(
        self,
        indicator: str,
        station_code: str,
        averaging_time: str,
        datetimes: list[datetime] | None = None,
        values: list[float | None] | None = None,
        unit: str | None = None,
    ):
        self.indicator = indicator
        self.station_code = station_code
        self.averaging_time = averaging_time
        self.datetimes = list(datetimes or [])
        self.values = list(values or [])
        self.unit = unit

        self.mean, self.stddev = self._calculate_stats()

        if len(self.datetimes) != len(self.values):
            raise ValueError("datetimes and values must have the same length")

    def _calculate_stats(self) -> tuple[float | None, float | None]:
        valid_values = [value for value in self.values if value is not None]
        if not valid_values:
            return None, None

        mean = sum(valid_values) / len(valid_values)
        variance = sum((value - mean) ** 2 for value in valid_values) / len(
            valid_values
        )
        stddev = variance**0.5
        return mean, stddev

    def __str__(self) -> str:
        unit = f" {self.unit}" if self.unit else ""
        return f"{self.station_code} {self.indicator} ({self.averaging_time}){unit}"

    def __repr__(self) -> str:
        return (
            f"TimeSeries(indicator={self.indicator}, station_code={self.station_code}, "
            f"averaging_time={self.averaging_time}, datetimes={self.datetimes}, "
            f"values={self.values}, unit={self.unit})"
        )

    def __getitem__(
        self, item
    ) -> (
        tuple[datetime, float | None]
        | list[tuple[datetime, float | None]]
        | list[float | None]
        | float
        | None
    ):
        if isinstance(item, slice):
            return list(zip(self.datetimes[item], self.values[item]))

        if isinstance(item, datetime):
            values = [
                value
                for timestamp, value in zip(self.datetimes, self.values)
                if timestamp == item
            ]
            if not values:
                raise KeyError(item)
            return values[0] if len(values) == 1 else values

        if isinstance(item, date):
            values = [
                value
                for timestamp, value in zip(self.datetimes, self.values)
                if timestamp.date() == item
            ]
            if not values:
                raise KeyError(item)
            return values

        timestamp, value = self.datetimes[item], self.values[item]
        return timestamp, value

    def __add__(self, other):
        if not isinstance(other, TimeSeries):
            raise TypeError("Can only add TimeSeries to TimeSeries")

        if (
            self.indicator != other.indicator
            or self.station_code != other.station_code
            or self.averaging_time != other.averaging_time
        ):
            raise ValueError(
                "TimeSeries must have the same indicator, station_code, and averaging_time to be added"
            )

        new_datetimes = self.datetimes + other.datetimes
        new_values = self.values + other.values
        new_unit = self.unit if self.unit == other.unit else None

        return TimeSeries(
            indicator=self.indicator,
            station_code=self.station_code,
            averaging_time=self.averaging_time,
            datetimes=new_datetimes,
            values=new_values,
            unit=new_unit,
        )


def main():
    series1 = TimeSeries(
        indicator="PM10",
        station_code="ST001",
        averaging_time="1h",
        datetimes=[datetime(2026, 1, 1, 0), datetime(2026, 1, 1, 1)],
        values=[10.0, 20.0],
        unit="some unit",
    )

    series2 = TimeSeries(
        indicator="PM10",
        station_code="ST001",
        averaging_time="1h",
        datetimes=[datetime(2026, 1, 1, 2)],
        values=[12.0],
        unit="some unit",
    )

    print(f"Series 1: {series1!r}")
    print(f"Series 2: {series2!r}")
    print(f"Series 1 mean: {series1.mean}, stddev: {series1.stddev}")
    print(f"Series 2 mean: {series2.mean}, stddev: {series2.stddev}")

    combined_series = series1 + series2
    print(f"Combined series: {combined_series!r}")
    print(f"Mean: {combined_series.mean}, Stddev: {combined_series.stddev}")


if __name__ == "__main__":
    main()