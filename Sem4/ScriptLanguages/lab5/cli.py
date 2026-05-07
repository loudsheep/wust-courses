import argparse
import re
import sys
import random
import logging
import statistics
from datetime import datetime

from parsing import parse_station_metadata, parse_measurements
from files import group_measurement_files_by_key


class StdoutFilter(logging.Filter):
    def filter(self, record):
        return record.levelno < logging.ERROR


def setup_logging():
    logger = logging.getLogger()
    logger.setLevel(logging.DEBUG)

    formatter = logging.Formatter("%(asctime)s - %(levelname)s - %(message)s")

    stdout_handler = logging.StreamHandler(sys.stdout)
    stdout_handler.setLevel(logging.DEBUG)
    stdout_handler.addFilter(StdoutFilter())
    stdout_handler.setFormatter(formatter)

    stderr_handler = logging.StreamHandler(sys.stderr)
    stderr_handler.setLevel(logging.ERROR)
    stderr_handler.setFormatter(formatter)

    logger.addHandler(stdout_handler)
    logger.addHandler(stderr_handler)


def valid_measurement(measurement_str):
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
        # "PM2225", # for testing
        "PrekursoryZielonka",
        "SO2",
    }

    if normalized not in allowed:
        allowed_hint = "PM10, PM2.5 (PM25), NO, NO2, NOx, CO, O3, SO2, ..."
        raise argparse.ArgumentTypeError(
            f"Nieprawidlowa wielkosc: {measurement_str}. Przyklad poprawnych wartosci: {allowed_hint}"
        )
    return normalized


def valid_date(date_str):
    if not re.match(r"^\d{4}-\d{2}-\d{2}$", date_str):
        raise argparse.ArgumentTypeError(
            f"Nieprawidlowy format daty: {date_str}. Oczekiwany format: RRRR-MM-DD"
        )
    try:
        return datetime.strptime(date_str, "%Y-%m-%d")
    except ValueError:
        raise argparse.ArgumentTypeError(
            f"Nieprawidlowa data: {date_str}. Oczekiwany format: RRRR-MM-DD"
        )


def parse_measurement_timestamp(timestamp):
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


def get_lowest_average_station(file_path, start_date, end_date):
    data = parse_measurements(file_path)
    if not data:
        return None

    averages = {}
    for station_data in data:
        values = []
        for measurement_data in station_data.measurements:
            moment = parse_measurement_timestamp(measurement_data.timestamp)
            if start_date <= moment <= end_date:
                values.append(measurement_data.value)
        if values:
            averages[station_data.station_code] = statistics.mean(values)

    if not averages:
        return None

    lowest_station_code = min(averages, key=averages.get)
    return lowest_station_code, averages[lowest_station_code]


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


def main():
    parser = argparse.ArgumentParser(
        description="CLI do analizy zanieczyszczeń powietrza."
    )

    parser.add_argument(
        "--measurement",
        required=True,
        type=valid_measurement,
        help="np. PM2.5, PM10, NO2",
    )
    parser.add_argument(
        "--mesurement",
        dest="measurement",
        type=valid_measurement,
        help=argparse.SUPPRESS,
    )
    parser.add_argument(
        "--frequency",
        required=True,
        choices=["1g", "24g", "1m"],
        help="Częstotliwość pomiarów: 1g (godzinowa), 24g (dobowa), 1m (miesięczna)",
    )
    parser.add_argument(
        "--start-date",
        required=True,
        type=valid_date,
        help="Data początkowa w formacie RRRR-MM-DD",
    )
    parser.add_argument(
        "--end-date",
        required=True,
        type=valid_date,
        help="Data końcowa w formacie RRRR-MM-DD",
    )

    subparsers = parser.add_subparsers(
        dest="command", required=True, help="Dostępne polecenia"
    )

    subparsers.add_parser(
        "random", help="Wypisuje nazwę i adres losowej stacji dla danych parametrów"
    )
    subparsers.add_parser(
        "best-station",
        help="Znajduje stacje z najniższą średnią danej wartości dla danego okresu",
    )
    parser_stats = subparsers.add_parser(
        "stats", help="Oblicza średnią i odchylenie standardowe"
    )
    parser_stats.add_argument(
        "--station", required=True, help="Kod stacji (np. DsWrocKorzA08)"
    )

    args = parser.parse_args()
    logger = logging.getLogger(__name__)

    if args.start_date > args.end_date:
        logger.error("--start-date jest pozniejsza niz --end-date")
        parser.error("--start-date nie moze byc pozniejsza niz --end-date")

    file_map = group_measurement_files_by_key("data/measurements")
    key = (str(args.start_date.year), args.measurement, args.frequency)
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

    end_date_inclusive = args.end_date.replace(hour=23, minute=59, second=59)

    if args.command == "random":
        active_codes = get_active_codes_from_file(
            target_file, args.start_date, end_date_inclusive
        )
        stations_in_range = [
            station for station in all_stations if station.id in active_codes
        ]

        if not stations_in_range:
            logger.warning(
                f"Brak stacji mierzacych {args.measurement} w przedziale "
                f"{args.start_date.date()} - {args.end_date.date()}"
            )
            print("Brak stacji mierzacych te wielkosc w podanym przedziale czasowym.")
            sys.exit(1)

        random_station = random.choice(stations_in_range)
        print(
            f"Losowa stacja: {random_station.name}, Adres: {random_station.address}, Kod: {random_station.id}"
        )

    elif args.command == "stats":
        station_values = get_station_measurements_in_range(
            target_file,
            args.station,
            args.start_date,
            end_date_inclusive,
        )

        if not station_values:
            logger.warning(
                f"Brak pomiarow dla stacji {args.station} ({args.measurement}) w przedziale "
                f"{args.start_date.date()} - {args.end_date.date()}"
            )
            print(
                f"Brak danych dla stacji {args.station} w podanym okresie "
                f"({args.start_date.date()} - {args.end_date.date()})."
            )
            sys.exit(1)

        mean_value = statistics.mean(station_values)
        std_dev = statistics.stdev(station_values) if len(station_values) > 1 else 0.0

        print(f"Stacja: {args.station}")
        print(f"Liczba pomiarow: {len(station_values)}")
        print(f"Srednia: {mean_value:.6f}")
        print(f"Odchylenie standardowe: {std_dev:.6f}")

    elif args.command == "best-station":
        res = get_lowest_average_station(target_file, args.start_date, end_date_inclusive)
        if not res:
            logger.warning("Brak danych w wyniku")
            print("Brak danych w wyniku")
            sys.exit(1)
        
        code, value = res
        print(f"Stacja z najmniejszą średnią dla {args.measurement} w przedziale ({args.start_date.date()} - {args.end_date.date()}) to {code} z wartością {value}")




if __name__ == "__main__":
    setup_logging()
    main()
