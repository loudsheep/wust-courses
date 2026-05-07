import csv
import re

import parsing


pattern_date = re.compile(r'(\d{4})-(\d{2})-(\d{2})') # yyyy-mm-dd
pattern_coord = re.compile(r'(-?\d+\.\d{6})') # number with 6 decimal places
pattern_2_parts = re.compile(r'.* - .*')
pl_to_en = str.maketrans('ąćęłńóśźżĄĆĘŁŃÓŚŹŻ', 'acelnoszzACELNOSZZ')
pattern_3_parts = re.compile(r'^[^-\n]+-[^-\n]+-[^-\n]+$')
pattern_street = re.compile(r'^(?=.*,)(?=.*(?:ul\.|al\.))', re.IGNORECASE)
pattern_mob = re.compile(r'.*MOB$')


def zadanie4(file='data/stacje.csv'):
    stations = parsing.parse_station_metadata(file)

    all_dates = []
    all_coords = []
    two_part_stations = []
    transformed_names = []
    all_mobile_stations_are_mob = True
    three_part_stations = []
    all_street_addresses = []
    
    for station in stations:
        dates = pattern_date.findall(f"{station.date_opened} {station.date_closed}")
        if dates:
            all_dates.extend(dates)

        coords = pattern_coord.findall(f"{station.latitude} {station.longitude}")
        if coords:
            all_coords.extend(coords)

        if pattern_2_parts.match(station.name):
            two_part_stations.append(station.name)

        name_transformed = re.sub(r' ', '_', station.name).translate(pl_to_en)
        transformed_names.append(name_transformed)
        
        if pattern_mob.match(station.id) and station.station_type.lower() != 'mobilna':
            all_mobile_stations_are_mob = False
            # print(f"Stacja {station.id} ma typ '{station.station_type}', ale jej ID sugeruje, że powinna być typu 'Mobilna'.")

        if pattern_3_parts.match(station.address):
            three_part_stations.append(station.address)

        if station.address and pattern_street.search(station.address):
            all_street_addresses.append(station.address)

    # print(f"Wszystkie znalezione daty: {all_dates}")
    # print(f"Wszystkie znalezione współrzędne: {all_coords}")
    # print(f"Stacje z nazwą składającą się z dwóch części: {two_part_stations}")
    # print(f"Stacje z adresem składającym się z trzech części: {three_part_stations}")
    # print(f"Wszystkie adresy uliczne: {all_street_addresses}")

    return (all_dates, all_coords, two_part_stations, transformed_names, all_mobile_stations_are_mob, three_part_stations, all_street_addresses)

if __name__ == '__main__':
    zadanie4()