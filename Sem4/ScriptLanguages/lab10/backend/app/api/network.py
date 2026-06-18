from collections import defaultdict
from itertools import groupby

from fastapi import APIRouter, Depends, Query
from pydantic import BaseModel
from sqlalchemy.orm import Session

from app.db.session import get_db
from app.models import Stop, StopTime

router = APIRouter()


class HeatmapEdge(BaseModel):
    from_stop_id: str
    from_lat: float
    from_lon: float
    to_stop_id: str
    to_lat: float
    to_lon: float
    frequency: int


@router.get("/network/heatmap", response_model=list[HeatmapEdge])
def get_heatmap(limit: int = Query(4000, ge=1, le=20000), db: Session = Depends(get_db)):
    stops = db.query(Stop).all()

    stops_by_name: dict[str, list[Stop]] = defaultdict(list)
    for stop in stops:
        stops_by_name[stop.stop_name].append(stop)

    stop_id_to_name = {stop.stop_id: stop.stop_name for stop in stops}
    name_to_node = {
        name: (
            min(s.stop_id for s in members),
            sum(s.stop_lat for s in members) / len(members),
            sum(s.stop_lon for s in members) / len(members),
        )
        for name, members in stops_by_name.items()
    }

    stop_times = (
        db.query(StopTime.trip_id, StopTime.stop_id)
        .order_by(StopTime.trip_id, StopTime.stop_sequence)
        .all()
    )

    edge_frequency: dict[tuple[str, str], int] = defaultdict(int)
    for _, trip_stop_times in groupby(stop_times, key=lambda row: row.trip_id):
        names = [stop_id_to_name[row.stop_id] for row in trip_stop_times]

        route = []
        for name in names:
            if not route or route[-1] != name:
                route.append(name)

        for from_name, to_name in zip(route, route[1:]):
            edge_key = tuple(sorted((from_name, to_name)))
            edge_frequency[edge_key] += 1

    top_edges = sorted(edge_frequency.items(), key=lambda item: item[1], reverse=True)[:limit]

    return [
        HeatmapEdge(
            from_stop_id=(from_node := name_to_node[from_name])[0],
            from_lat=from_node[1],
            from_lon=from_node[2],
            to_stop_id=(to_node := name_to_node[to_name])[0],
            to_lat=to_node[1],
            to_lon=to_node[2],
            frequency=frequency,
        )
        for (from_name, to_name), frequency in top_edges
    ]
