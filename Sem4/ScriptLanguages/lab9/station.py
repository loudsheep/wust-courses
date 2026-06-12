from datetime import datetime
from typing import Any


class Station:
    def __init__(
        self,
        id: str,
        name: str | None = None,
        city: str | None = None,
        voivodeship: str | None = None,
        address: str | None = None,
        latitude: float | str | None = None,
        longitude: float | str | None = None,
        date_opened: str | datetime | None = None,
        date_closed: str | datetime | None = None,
        station_type: str | None = None,
        area_type: str | None = None,
        station_kind: str | None = None,
        international_code: str | None = None,
        old_code: str | None = None,
    ):
        self.id = id
        self.code = id
        self.name = name
        self.city = city
        self.voivodeship = voivodeship
        self.address = address
        self.latitude = latitude
        self.longitude = longitude
        self.date_opened = date_opened
        self.date_closed = date_closed
        self.station_type = station_type
        self.area_type = area_type
        self.station_kind = station_kind
        self.international_code = international_code
        self.old_code = old_code

    def __str__(self) -> str:
        parts = [self.code]
        if self.name:
            parts.append(self.name)
        if self.city:
            parts.append(self.city)
        return " - ".join(parts)

    def __repr__(self) -> str:
        return (
            f"Station(id={self.id}, name={self.name}, city={self.city}, "
            f"voivodeship={self.voivodeship}, address={self.address}, "
            f"latitude={self.latitude}, longitude={self.longitude}, "
            f"date_opened={self.date_opened}, date_closed={self.date_closed}, "
            f"station_type={self.station_type}, area_type={self.area_type}, "
            f"station_kind={self.station_kind}, international_code={self.international_code}, "
            f"old_code={self.old_code})"
        )

    def __eq__(self, other: Any) -> bool:
        if not isinstance(other, Station):
            return False
        return self.code == other.code
