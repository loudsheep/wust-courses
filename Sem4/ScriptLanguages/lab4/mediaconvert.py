import argparse
import os
import subprocess
from datetime import datetime

from utils import (
    get_file_type,
    get_output_directory,
    list_media_files,
    make_output_name,
    read_history,
    write_history,
)


def convert_file(input_path, output_path, file_type):
    if file_type == "image":
        cmd = ["convert", input_path, output_path]
        program = "convert"
    else:
        cmd = ["ffmpeg", "-y", "-i", input_path, output_path]
        program = "ffmpeg"

    result = subprocess.run(cmd, capture_output=True, text=True)
    return result.returncode == 0, program


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("directory", help="directory with media files")
    parser.add_argument("--format", required=True, help="target format, ex. webm or mp3")
    args = parser.parse_args()

    if not os.path.isdir(args.directory):
        print("Provided path is not a directory")
        return

    output_dir = get_output_directory()
    history_path = os.path.join(output_dir, "history.json")
    history = read_history(history_path)

    for input_path in list_media_files(args.directory):
        file_type = get_file_type(input_path)
        if file_type == "unknown":
            continue

        output_name = make_output_name(input_path, args.format)
        output_path = os.path.join(output_dir, output_name)

        ok, program = convert_file(input_path, output_path, file_type)
        if not ok:
            print(f"Conversion failed: {input_path}")
            continue

        history.append(
            {
                "date": datetime.now().isoformat(timespec="seconds"),
                "source_path": input_path,
                "output_format": args.format.lstrip("."),
                "output_path": output_path,
                "program": program,
            }
        )
        print(f"Converted: {input_path} -> {output_path}")

    write_history(history_path, history)


if __name__ == "__main__":
    main()
