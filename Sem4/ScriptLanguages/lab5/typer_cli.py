import typer
import random
import logging
import statistics
from datetime import datetime
import re
from typing import Optional
from parsing import parse_station_metadata, parse_measurements
from files import group_measurement_files_by_key
import anomalies as a

app = typer.Typer()

class StdoutFilter(logging.Filter):
    def filter(self, record):
        return record.levelno < logging.ERROR


def setup_logging():
    logger = logging.getLogger()
    logger.setLevel(logging.DEBUG)

    formatter = logging.Formatter("%(asctime)s - %(levelname)s - %(message)s")

    stdout_handler = logging.StreamHandler()
    stdout_handler.setLevel(logging.DEBUG)
    stdout_handler.addFilter(StdoutFilter())
    stdout_handler.setFormatter(formatter)

    stderr_handler = logging.StreamHandler()
    stderr_handler.setLevel(logging.ERROR)
    stderr_handler.setFormatter(formatter)

    logger.addHandler(stdout_handler)
    logger.addHandler(stderr_handler)


def valid_measurement(measurement_str: str) -> str:
    aliases = {
        "PM2.5": "PM25",
        "PM2,5": "PM25",
    }
    normalized = aliases.get(measurement_str, measurement_str)

    allowed = {
        "As(PM10)",
        "BaA(PM10)",
        "BaP(PM10)",
        "BbF(PM10)",
        "BjF(PM10)",
        "BkF(PM10)",
        "C6H6",
        "Cd(PM10)",
        "CO",
        "DBahA(PM10)",
        "Depozycja",
        "formaldehyd",
        "Hg(TGM)",
        "IP(PM10)",
        "Jony",
        "Ni(PM10)",
        "NO2",
        "NOx",
        "NO",
        "O3",
        "Pb(PM10)",
        "PM10",
        "PM25",
        "PrekursoryZielonka",
        "SO2",
    }

    if normalized not in allowed:
        allowed_hint = "PM10, PM2.5 (PM25), NO, NO2, NOx, CO, O3, SO2, ..."
        raise typer.BadParameter(
            f"Nieprawidlowa wielkosc: {measurement_str}. Przyklad poprawnych wartosci: {allowed_hint}"
        )
    return normalized


def valid_date(date_str: str) -> datetime:
    if not re.match(r"^\d{4}-\d{2}-\d{2}$", date_str):
        raise typer.BadParameter(
            f"Nieprawidlowy format daty: {date_str}. Oczekiwany format: RRRR-MM-DD"
        )
    try:
        return datetime.strptime(date_str, "%Y-%m-%d")
    except ValueError:
        raise typer.BadParameter(
            f"Nieprawidlowa data: {date_str}. Oczekiwany format: RRRR-MM-DD"
        )


def parse_measurement_timestamp(timestamp: str) -> datetime:
    for fmt in ("%m/%d/%y %H:%M", "%d/%m/%y %H:%M"):
        try:
            return datetime.strptime(timestamp, fmt)
        except ValueError:
            continue
    raise ValueError(f"Nieobslugiwany format daty pomiaru: {timestamp}")


def get_active_codes_from_file(file_path, start_date, end_date):
    data = parse_measurements(file_path)
    if not data:
        return set()

    active_codes = set()
    for station_data in data:
        for measurement in station_data.measurements:
            moment = parse_measurement_timestamp(measurement.timestamp)
            if start_date <= moment <= end_date:
                active_codes.add(station_data.station_code)
                break

    return active_codes


def get_station_measurements_in_range(file_path, station_code, start_date, end_date):
    data = parse_measurements(file_path)
    if not data:
        return []

    station_data = next(
        (item for item in data if item.station_code == station_code), None
    )
    if station_data is None:
        return []

    values = []
    for measurement in station_data.measurements:
        moment = parse_measurement_timestamp(measurement.timestamp)
        if start_date <= moment <= end_date:
            values.append(measurement.value)

    return values


@app.command()
def random_station(
    measurement: str = typer.Option(..., help="np. PM2.5, PM10, NO2", callback=valid_measurement),
    frequency: str = typer.Option(..., help="Częstotliwość pomiarów: 1g (godzinowa), 24g (dobowa), 1m (miesięczna)"),
    start_date: str = typer.Option(..., help="Data początkowa w formacie RRRR-MM-DD", callback=valid_date),
    end_date: str = typer.Option(..., help="Data końcowa w formacie RRRR-MM-DD", callback=valid_date)
):
    setup_logging()
    logger = logging.getLogger(__name__)

    if start_date > end_date:
        logger.error("--start-date jest pozniejsza niz --end-date")
        raise typer.BadParameter("--start-date nie moze byc pozniejsza niz --end-date")

    file_map = group_measurement_files_by_key("data/measurements")
    key = (str(start_date.year), measurement, frequency)
    target_file = file_map.get(key)

    if not target_file:
        logger.error(
            f"Plik nie znaleziony dla klucza: {key}. Sprawdz wielkosc, czestotliwosc i rok."
        )
        sys.exit(1)

    try:
        all_stations = parse_station_metadata("data/stacje.csv")
    except FileNotFoundError:
        logger.error("Nie mozna otworzyc pliku metadanych stacji (data/stacje.csv)")
        sys.exit(1)

    end_date_inclusive = end_date.replace(hour=23, minute=59, second=59)

    active_codes = get_active_codes_from_file(target_file, start_date, end_date_inclusive)

    if not active_codes:
        logger.warning("Brak stacji mierzacych w podanym okresie.")
        sys.exit(1)

    stations_in_range = [station for station in all_stations if station.id in active_codes]

    if not stations_in_range:
        logger.warning(f"Brak stacji mierzacych {measurement} w przedziale {start_date.date()} - {end_date.date()}")
        sys.exit(1)

    random_station = random.choice(stations_in_range)
    print(f"Losowa stacja: {random_station.name}, Adres: {random_station.address}, Kod: {random_station.id}")


@app.command()
def stats(
    station: str = typer.Option(..., help="Kod stacji (np. DsWrocKorzA08)"),
    measurement: str = typer.Option(..., help="np. PM2.5, PM10, NO2", callback=valid_measurement),
    frequency: str = typer.Option(..., help="Częstotliwość pomiarów: 1g (godzinowa), 24g (dobowa), 1m (miesięczna)"),
    start_date: str = typer.Option(..., help="Data początkowa w formacie RRRR-MM-DD", callback=valid_date),
    end_date: str = typer.Option(..., help="Data końcowa w formacie RRRR-MM-DD", callback=valid_date)
):
    setup_logging()
    logger = logging.getLogger(__name__)

    if start_date > end_date:
        logger.error("--start-date jest pozniejsza niz --end-date")
        raise typer.BadParameter("--start-date nie moze byc pozniejsza niz --end-date")

    file_map = group_measurement_files_by_key("data/measurements")
    key = (str(start_date.year), measurement, frequency)
    target_file = file_map.get(key)

    if not target_file:
        logger.error(
            f"Plik nie znaleziony dla klucza: {key}. Sprawdz wielkosc, czestotliwosc i rok."
        )
        sys.exit(1)

    try:
        all_stations = parse_station_metadata("data/stacje.csv")
    except FileNotFoundError:
        logger.error("Nie mozna otworzyc pliku metadanych stacji (data/stacje.csv)")
        sys.exit(1)

    end_date_inclusive = end_date.replace(hour=23, minute=59, second=59)

    station_values = get_station_measurements_in_range(target_file, station, start_date, end_date_inclusive)

    if not station_values:
        logger.warning(f"Brak pomiarow dla stacji {station} ({measurement}) w przedziale {start_date.date()} - {end_date.date()}")
        sys.exit(1)

    mean_value = statistics.mean(station_values)
    std_dev = statistics.stdev(station_values) if len(station_values) > 1 else 0.0

    print(f"Stacja: {station}")
    print(f"Liczba pomiarow: {len(station_values)}")
    print(f"Srednia: {mean_value:.6f}")
    print(f"Odchylenie standardowe: {std_dev:.6f}")

@app.command()
def detect_anomalies(file_path: str, delta_threshold: float = 50, alarm_threshold: float = 500, zero_threshold: int = 5):
    stations = parse_measurements(file_path)
    if not stations:
        typer.echo("Brak danych do analizy.")
        raise typer.Exit()

    for station in stations:
        anomalies = a.detect_anomalies(station.measurements, alarm_threshold, delta_threshold, zero_threshold)
        if anomalies:
            typer.echo(f"Anomalie dla stacji {station.station_code}:")
            for anomaly in anomalies:
                typer.echo(f" - {anomaly}")
        else:
            typer.echo(f"Brak anomalii dla stacji {station.station_code}")

if __name__ == "__main__":
    app()
