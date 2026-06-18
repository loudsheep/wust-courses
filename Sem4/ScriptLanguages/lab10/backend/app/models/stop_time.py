from __future__ import annotations

from sqlalchemy import ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db.base import Base


class StopTime(Base):
    __tablename__ = "stop_times"

    trip_id: Mapped[str] = mapped_column(ForeignKey("trips.trip_id"), primary_key=True)
    stop_sequence: Mapped[int] = mapped_column(primary_key=True)
    arrival_time: Mapped[str | None] = mapped_column(nullable=True)
    departure_time: Mapped[str | None] = mapped_column(nullable=True)
    stop_id: Mapped[str] = mapped_column(ForeignKey("stops.stop_id"), nullable=False, index=True)

    trip: Mapped["Trip"] = relationship(back_populates="stop_times")
    stop: Mapped["Stop"] = relationship(back_populates="stop_times")
