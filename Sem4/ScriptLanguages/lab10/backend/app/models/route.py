from __future__ import annotations

from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db.base import Base


class Route(Base):
    __tablename__ = "routes"

    route_id: Mapped[str] = mapped_column(primary_key=True)
    agency_id: Mapped[str | None] = mapped_column(nullable=True)
    route_short_name: Mapped[str | None] = mapped_column(nullable=True)
    route_long_name: Mapped[str | None] = mapped_column(nullable=True)
    route_desc: Mapped[str | None] = mapped_column(nullable=True)
    route_type: Mapped[int | None] = mapped_column(nullable=True)

    trips: Mapped[list["Trip"]] = relationship(back_populates="route")
