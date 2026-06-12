from abc import ABC, abstractmethod

from timeseries import TimeSeries


class SeriesValidator(ABC):
    @abstractmethod
    def analyze(self, series: TimeSeries) -> list[str]:
        pass


class OutlierDetector(SeriesValidator):
    def __init__(self, k: float):
        self.k = k

    def analyze(self, series: TimeSeries) -> list[str]:
        if series.mean is None or series.stddev is None:
            return []
        outliers = []
        for datetime, value in zip(series.datetimes, series.values):
            if value is not None and abs(value - series.mean) > self.k * series.stddev:
                outliers.append(f"Outlier detected at {datetime}: {value}")
        return outliers


class ZeroSpikeDetector(SeriesValidator):
    def __init__(self, n: int = 3):
        self.n = n

    def analyze(self, series: TimeSeries) -> list[str]:
        alerts = []
        consecutive_zeros = 0
        for datetime, value in zip(series.datetimes, series.values):
            if value is None or value == 0:
                consecutive_zeros += 1
            else:
                if consecutive_zeros >= self.n:
                    alerts.append(
                        f"Zero spike detected at {datetime}: {consecutive_zeros} consecutive zeros/missing values"
                    )
                consecutive_zeros = 0

        if consecutive_zeros >= self.n:
            alerts.append(
                f"Zero spike detected at end of series: {consecutive_zeros} consecutive zeros/missing values"
            )

        return alerts


class ThresholdDetector(SeriesValidator):
    def __init__(self, threshold: float):
        self.threshold = threshold

    def analyze(self, series: TimeSeries) -> list[str]:
        alerts = []
        for datetime, value in zip(series.datetimes, series.values):
            if value is not None and value > self.threshold:
                alerts.append(
                    f"Threshold exceeded at {datetime}: {value} > {self.threshold}"
                )
        return alerts

class SimpleReporter():
    def analyze(self, series: TimeSeries) -> list[str]:
        result: list[str] = []
        result.append(f"Info: {series.indicator} at {series.station_code} has mean = {series.mean}")
        return result

