from __future__ import annotations

from sqlalchemy import ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db.base import Base


class Trip(Base):
    __tablename__ = "trips"

    trip_id: Mapped[str] = mapped_column(primary_key=True)
    route_id: Mapped[str] = mapped_column(ForeignKey("routes.route_id"), nullable=False, index=True)
    service_id: Mapped[str] = mapped_column(ForeignKey("calendar.service_id"), nullable=False, index=True)
    trip_headsign: Mapped[str | None] = mapped_column(nullable=True)
    direction_id: Mapped[int | None] = mapped_column(nullable=True)

    route: Mapped["Route"] = relationship(back_populates="trips")
    calendar: Mapped["Calendar"] = relationship(back_populates="trips")
    stop_times: Mapped[list["StopTime"]] = relationship(back_populates="trip")
