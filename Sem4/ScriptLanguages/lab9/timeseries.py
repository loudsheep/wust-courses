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
        self, item: Any
    ) -> (
        tuple[datetime, float | None]
        | list[tuple[datetime, float | None]]
        | list[float | None]
        | float
        | None
    ):
        if isinstance(item, slice):
            result = list(zip(self.datetimes[item], self.values[item]))
            if not result:
                raise ValueError("Slice does not contain any data")
            return result

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
