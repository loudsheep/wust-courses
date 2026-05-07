import csv
import re
import logging
from collections import namedtuple

logger = logging.getLogger(__name__)

def parse_station_metadata(file_path):
    Station = namedtuple('Station', ['id', 'name', 'city', 'voivodeship', 'address', 'latitude', 'longitude', 'date_opened', 'date_closed', 'station_type'])
    logger.info(f"Otwarcie pliku: {file_path}")
    try:
        with open(file_path, mode='r', newline='', encoding='utf-8') as file:
            reader = csv.DictReader(file)
            stations = []
            for row in reader:
                id = row['Kod stacji']
                name = row['Nazwa stacji']
                city = row['Miejscowość']
                address = row['Adres']
                voivodeship = row['Województwo']
                latitude = row['WGS84 φ N']
                longitude = row['WGS84 λ E']
                date_opened = row['Data uruchomienia']
                date_closed = row['Data zamknięcia']
                station_type = row['Rodzaj stacji']
                row_data = [id, name, city, voivodeship, address, latitude, longitude, date_opened, date_closed, station_type]
                row_bytes = sum(len(str(cell).encode('utf-8')) for cell in row_data)
                logger.debug(f"Przeczytanych bajtów: {row_bytes}")
                stations.append(Station(id, name, city, voivodeship, address, latitude, longitude, date_opened, date_closed, station_type))
        logger.info(f"Zamknięcie pliku: {file_path}")
        return stations
    except FileNotFoundError:
        logger.error(f"Plik nie istnieje: {file_path}")
        raise


def parse_measurements(file_path):
    Measurement = namedtuple('Measurement', ['timestamp', 'value'])
    StationMeasurements = namedtuple(
        'StationMeasurements',
        ['station_number', 'station_code', 'indicator', 'averaging_time', 'unit', 'position_code', 'measurements']
    )

    datetime_pattern = re.compile(r'^\d{2}/\d{2}/\d{2} \d{2}:\d{2}$')

    def _is_datetime(text):
        return bool(text and datetime_pattern.match(text.strip()))

    logger.info(f"Otwarcie pliku: {file_path}")
    try:
        with open(file_path, mode='r', newline='', encoding='utf-8') as file:
            reader = list(csv.reader(file))
            for row in reader:
                row_bytes = sum(len(str(cell).encode('utf-8')) for cell in row)
                logger.debug(f"Przeczytanych bajtów: {row_bytes}")
    except FileNotFoundError:
        logger.error(f"Plik nie istnieje: {file_path}")
        raise

    if len(reader) < 2:
        logger.warning(f"Plik {file_path} ma mniej niż 2 wiersze (pusty lub nieprawidłowy format)")
        return None

    metadata_rows = {
        'Nr': None,
        'Kod stacji': None,
        'Wskaźnik': None,
        'Czas uśredniania': None,
        'Jednostka': None,
        'Kod stanowiska': None,
    }

    for row in reader:
        if not row:
            continue
        row_name = row[0].strip()
        if row_name in metadata_rows:
            metadata_rows[row_name] = row

    code_row = metadata_rows['Kod stacji']
    if not code_row:
        return []

    data_start_idx = None
    for i, row in enumerate(reader):
        if row and _is_datetime(row[0]):
            data_start_idx = i
            break

    if data_start_idx is None:
        return []

    stations = []
    for station_index in range(1, len(code_row)):
        station_code = code_row[station_index].strip()
        if not station_code:
            continue

        measurements = []
        for row in reader[data_start_idx:]:
            if not row or len(row) <= station_index:
                continue

            timestamp = row[0].strip()
            if not _is_datetime(timestamp):
                continue

            raw_value = row[station_index].strip().replace(',', '.')
            if not raw_value:
                continue

            try:
                value = float(raw_value)
                measurements.append(Measurement(timestamp, value))
            except ValueError:
                continue

        station_number = None
        if metadata_rows['Nr'] and len(metadata_rows['Nr']) > station_index:
            station_number = metadata_rows['Nr'][station_index].strip()

        indicator = None
        if metadata_rows['Wskaźnik'] and len(metadata_rows['Wskaźnik']) > station_index:
            indicator = metadata_rows['Wskaźnik'][station_index].strip()

        averaging_time = None
        if metadata_rows['Czas uśredniania'] and len(metadata_rows['Czas uśredniania']) > station_index:
            averaging_time = metadata_rows['Czas uśredniania'][station_index].strip()

        unit = None
        if metadata_rows['Jednostka'] and len(metadata_rows['Jednostka']) > station_index:
            unit = metadata_rows['Jednostka'][station_index].strip()

        position_code = None
        if metadata_rows['Kod stanowiska'] and len(metadata_rows['Kod stanowiska']) > station_index:
            position_code = metadata_rows['Kod stanowiska'][station_index].strip()

        stations.append(
            StationMeasurements(
                station_number=station_number,
                station_code=station_code,
                indicator=indicator,
                averaging_time=averaging_time,
                unit=unit,
                position_code=position_code,
                measurements=measurements,
            )
        )

    logger.info(f"Zamknięcie pliku: {file_path}")
    return stations
