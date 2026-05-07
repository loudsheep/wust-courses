import re
import parsing
import pathlib

def get_addresses(path, city):
    pattern = re.compile(
        r"(?:ul\.|al\.|pl\.)?\s*"
        r"(?P<street>[\wąćęłńóśźżĄĆĘŁŃÓŚŹŻ\s\/-]+?)"
        r"(?:\s+(?P<number>\d+[a-zA-Z]?(?:/\d+[a-zA-Z]?)?))?$"
    )
    stations = parsing.parse_station_metadata(path)
    result = []

    for station in stations:
        if station.city.lower() != city.lower():
            continue

        if not station.address:
            continue

        match = pattern.search(station.address)
        if match:
            street = match.group('street').strip()
            number = match.group('number')
        else:
            street = None
            number = None

        result.append((
            station.voivodeship,
            station.city,
            street,
            number
        ))

    return result
