import json
import os
from datetime import datetime

IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".tiff"}
AUDIO_VIDEO_EXTENSIONS = {
    ".mp3", ".wav", ".ogg", ".flac", ".m4a",
    ".mp4", ".mkv", ".avi", ".mov", ".webm", ".wmv",
}


def list_media_files(directory):
    files = []
    for name in os.listdir(directory):
        full_path = os.path.join(directory, name)
        if os.path.isfile(full_path):
            files.append(full_path)
    return files


def get_file_type(path):
    ext = os.path.splitext(path)[1].lower()
    if ext in IMAGE_EXTENSIONS:
        return "image"
    if ext in AUDIO_VIDEO_EXTENSIONS:
        return "audio_video"
    return "unknown"


def get_output_directory():
    output_dir = os.environ.get("CONVERTED_DIR")
    if not output_dir:
        output_dir = os.path.join(os.getcwd(), "converted")
    os.makedirs(output_dir, exist_ok=True)
    return output_dir

def get_environment_variable(name, default=None):
    return os.environ.get(name, default)

def make_output_name(input_path, output_format):
    timestamp = datetime.now().strftime("%Y%m%d-%H%M%S")
    base_name = os.path.splitext(os.path.basename(input_path))[0]
    clean_ext = output_format.lstrip(".")
    return f"{timestamp}-{base_name}.{clean_ext}"


def read_history(history_path):
    if not os.path.exists(history_path):
        return []
    with open(history_path, "r", encoding="utf-8") as f:
        return json.load(f)


def write_history(history_path, history):
    with open(history_path, "w", encoding="utf-8") as f:
        json.dump(history, f, ensure_ascii=False, indent=2)
