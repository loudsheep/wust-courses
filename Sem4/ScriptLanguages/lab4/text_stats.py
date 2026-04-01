import json
import re
import sys
from collections import Counter


def analyze_file(path):
    with open(path, "r", encoding="utf-8", errors="ignore") as f:
        text = f.read()

    words = re.findall(r"\w+", text.lower(), flags=re.UNICODE)
    char_counter = Counter(text)
    word_counter = Counter(words)

    result = {
        "path": path,
        "chars": len(text),
        "words": len(words),
        "lines": len(text.splitlines()),
        "most_common_char": char_counter.most_common(1)[0][0] if char_counter else "",
        "most_common_word": word_counter.most_common(1)[0][0] if word_counter else "",
        "char_counts": dict(char_counter),
        "word_counts": dict(word_counter),
    }
    return result


def main():
    path = sys.stdin.readline().strip()
    if not path:
        print(json.dumps({"error": "no input path"}))
        return

    try:
        result = analyze_file(path)
        print(json.dumps(result, ensure_ascii=False))
    except Exception as e:
        print(json.dumps({"error": str(e), "path": path}, ensure_ascii=False))


if __name__ == "__main__":
    main()
