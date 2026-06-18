from __future__ import annotations

from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db.base import Base


class Stop(Base):
    __tablename__ = "stops"

    stop_id: Mapped[str] = mapped_column(primary_key=True)
    stop_code: Mapped[str | None] = mapped_column(nullable=True)
    stop_name: Mapped[str] = mapped_column(nullable=False)
    stop_lat: Mapped[float] = mapped_column(nullable=False)
    stop_lon: Mapped[float] = mapped_column(nullable=False)

    stop_times: Mapped[list["StopTime"]] = relationship(back_populates="stop")
