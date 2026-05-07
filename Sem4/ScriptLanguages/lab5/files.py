from pathlib import Path
import re

def group_measurement_files_by_key(directory):
    dir = Path(directory)
    result = {}

    pattern = re.compile(r"(\d{4})_(.+?)_(.+)\.csv")
    for file in dir.iterdir():
        if not file.is_file():
            continue
        match = pattern.match(file.name)
        if match:
            year = match.group(1)
            size = match.group(2)
            freq = match.group(3)
            key = (year, size, freq)
            result[key] = file

    return result
