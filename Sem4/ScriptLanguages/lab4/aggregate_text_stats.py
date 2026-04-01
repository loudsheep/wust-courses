import json
import os
import subprocess
import sys
from collections import Counter


def get_files(directory):
    files = []
    for name in os.listdir(directory):
        full_path = os.path.join(directory, name)
        if os.path.isfile(full_path):
            files.append(full_path)
    return files


def run_single_analysis(script_path, file_path):
    process = subprocess.run(
        [sys.executable, script_path],
        input=file_path + "\n",
        text=True,
        capture_output=True,
    )

    if process.returncode != 0:
        return None

    output = process.stdout.strip()
    if not output:
        return None

    try:
        data = json.loads(output)
        if "error" in data:
            return None
        return data
    except json.JSONDecodeError:
        return None


def main():
    if len(sys.argv) != 2:
        print("Usage: python aggregate_text_stats.py <directory>")
        return

    directory = sys.argv[1]
    if not os.path.isdir(directory):
        print("Provided path is not a directory")
        return

    script_path = os.path.join(os.path.dirname(__file__), "text_stats.py")
    all_results = []

    total_chars = 0
    total_words = 0
    total_lines = 0
    all_chars = Counter()
    all_words = Counter()

    for file_path in get_files(directory):
        data = run_single_analysis(script_path, file_path)
        if data is None:
            continue

        all_results.append(data)
        total_chars += data.get("chars", 0)
        total_words += data.get("words", 0)
        total_lines += data.get("lines", 0)
        all_chars.update(data.get("char_counts", {}))
        all_words.update(data.get("word_counts", {}))

    summary = {
        "files_read": len(all_results),
        "total_chars": total_chars,
        "total_words": total_words,
        "total_lines": total_lines,
        "most_common_char": all_chars.most_common(1)[0][0] if all_chars else "",
        "most_common_word": all_words.most_common(1)[0][0] if all_words else "",
    }

    print(json.dumps(summary, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
