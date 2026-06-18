from __future__ import annotations

from datetime import date

from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db.base import Base


class Calendar(Base):
    __tablename__ = "calendar"

    service_id: Mapped[str] = mapped_column(primary_key=True)
    monday: Mapped[bool] = mapped_column(nullable=False)
    tuesday: Mapped[bool] = mapped_column(nullable=False)
    wednesday: Mapped[bool] = mapped_column(nullable=False)
    thursday: Mapped[bool] = mapped_column(nullable=False)
    friday: Mapped[bool] = mapped_column(nullable=False)
    saturday: Mapped[bool] = mapped_column(nullable=False)
    sunday: Mapped[bool] = mapped_column(nullable=False)
    start_date: Mapped[date] = mapped_column(nullable=False)
    end_date: Mapped[date] = mapped_column(nullable=False)

    trips: Mapped[list["Trip"]] = relationship(back_populates="calendar")
