from fastapi import Query
from sqlalchemy import func, desc
from app.models import StopTime, Trip, Route, Stop
from sqlalchemy.orm import Session
from fastapi import APIRouter, Depends
from app.db.session import get_db

from pydantic import BaseModel

router = APIRouter()


class DirectionStat(BaseModel):
    direction: str | None
    count: int


class StopStats(BaseModel):
    stopId: str
    lineCount: int
    departureCount: int
    earliestDeparture: str | None
    latestDeparture: str | None
    topDirections: list[DirectionStat]


@router.get("/stops/{stop_id}", response_model=StopStats)
def get_stop_stats(stop_id: str, db: Session = Depends(get_db)):
    stop = db.query(Stop).filter(Stop.stop_id == stop_id).first()
    if not stop:
        raise HTTPException(status_code=404, detail="Stop not found")

    line_count = (
        db.query(func.count(func.distinct(Route.route_id)))
        .join(Trip, Trip.route_id == Route.route_id)
        .join(StopTime, StopTime.trip_id == Trip.trip_id)
        .filter(StopTime.stop_id == stop_id)
        .scalar()
    )

    departure_count = (
        db.query(func.count())
        .select_from(StopTime)
        .filter(StopTime.stop_id == stop_id)
        .scalar()
    )

    earliest, latest = (
        db.query(
            func.min(StopTime.departure_time),
            func.max(StopTime.departure_time),
        )
        .filter(StopTime.stop_id == stop_id)
        .one()
    )

    directions = (
        db.query(
            Trip.trip_headsign.label("direction"),
            func.count().label("count"),
        )
        .join(StopTime, StopTime.trip_id == Trip.trip_id)
        .filter(StopTime.stop_id == stop_id)
        .group_by(Trip.trip_headsign)
        .order_by(desc("count"))
        .limit(5)
        .all()
    )

    return {
        "stopId": stop_id,
        "lineCount": line_count or 0,
        "departureCount": departure_count or 0,
        "earliestDeparture": earliest,
        "latestDeparture": latest,
        "topDirections": [
            {"direction": d.direction or "Unknown", "count": d.count}
            for d in directions
        ],
    }


@router.get("/stops")
def get_stops(db: Session = Depends(get_db)):
    stops = db.query(Stop).all()

    return [
        {
            "stopId": s.stop_id,
            "name": s.stop_name,
            "stopCode": s.stop_code,
            "lat": s.stop_lat,
            "lon": s.stop_lon,
        }
        for s in stops
    ]
