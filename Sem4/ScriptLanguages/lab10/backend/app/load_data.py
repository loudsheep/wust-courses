import zipfile
import csv
import io
from sqlalchemy.orm import Session
from app.models import Stop, Route, Calendar, Trip, StopTime


def load_csv_from_zip(zip_file, filename):
    with zip_file.open(filename) as file:
        text = io.TextIOWrapper(file, encoding="utf-8-sig")
        reader = csv.DictReader(text)
        for row in reader:
            yield row


def load_data_from_file(file_path: str, db: Session):
    with zipfile.ZipFile(file_path) as z:
        process_zip(z, db)


def process_zip(z: zipfile.ZipFile, db: Session):
    # Clear existing data to avoid IntegrityErrors
    db.query(StopTime).delete()
    db.query(Trip).delete()
    db.query(Stop).delete()
    db.query(Route).delete()
    db.query(Calendar).delete()
    db.commit()

    for row in load_csv_from_zip(z, "stops.txt"):
        db.add(
            Stop(
                stop_id=row["stop_id"],
                stop_code=row.get("stop_code"),
                stop_name=row["stop_name"],
                stop_lat=float(row["stop_lat"]),
                stop_lon=float(row["stop_lon"]),
            )
        )
    db.commit()

    for row in load_csv_from_zip(z, "routes.txt"):
        db.add(
            Route(
                route_id=row["route_id"],
                agency_id=row.get("agency_id"),
                route_short_name=row.get("route_short_name"),
                route_long_name=row.get("route_long_name"),
                route_desc=row.get("route_desc"),
                route_type=int(row["route_type"]),
            )
        )
    db.commit()

    from datetime import datetime

    for row in load_csv_from_zip(z, "calendar.txt"):
        db.add(
            Calendar(
                service_id=row["service_id"],
                monday=bool(int(row["monday"])),
                tuesday=bool(int(row["tuesday"])),
                wednesday=bool(int(row["wednesday"])),
                thursday=bool(int(row["thursday"])),
                friday=bool(int(row["friday"])),
                saturday=bool(int(row["saturday"])),
                sunday=bool(int(row["sunday"])),
                start_date=datetime.strptime(row["start_date"], "%Y%m%d").date(),
                end_date=datetime.strptime(row["end_date"], "%Y%m%d").date(),
            )
        )
    db.commit()

    for row in load_csv_from_zip(z, "trips.txt"):
        db.add(
            Trip(
                route_id=row["route_id"],
                service_id=row["service_id"],
                trip_id=row["trip_id"],
                trip_headsign=row.get("trip_headsign"),
                direction_id=int(row["direction_id"])
                if row.get("direction_id")
                else None,
            )
        )
    db.commit()

    batch = []
    for row in load_csv_from_zip(z, "stop_times.txt"):
        batch.append(
            StopTime(
                trip_id=row["trip_id"],
                arrival_time=row["arrival_time"],
                departure_time=row["departure_time"],
                stop_id=row["stop_id"],
                stop_sequence=int(row["stop_sequence"]),
            )
        )

        if len(batch) == 1000:
            db.bulk_save_objects(batch)
            db.commit()
            batch.clear()

    if batch:
        db.bulk_save_objects(batch)
        db.commit()
