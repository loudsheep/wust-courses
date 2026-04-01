import sys
import os
import argparse

from utils import get_environment_variable

parser = argparse.ArgumentParser()
parser.add_argument("file", nargs="?", help="file to read")
parser.add_argument("--lines", type=int, default=10, help="number of lines to display")
parser.add_argument("--follow", action="store_true", help="keep following the file")
args = parser.parse_args()


def tail_lines(lines, n):
    reverse = get_environment_variable("TAIL_REVERSE", "0") == "1"
    if reverse:
        lines = lines[-n:][::-1]
    return lines[-n:]


def tail_chars(lines, n):
    text = "".join(lines)
    return text[-n:]


def read_file(path):
    with open(path, "r") as f:
        return f.readlines()


def follow_file(path, n):
    with open(path, "r") as f:
        lines = f.readlines()
        for line in handle_mode(lines, n):
            print(line, end="")
        f.seek(0, os.SEEK_END)
        while True:
            line = f.readline()
            if line:
                print(line, end="")
            else:
                import time

                print("wait")
                time.sleep(1)


def handle_mode(lines, n):
    mode = get_environment_variable("TAIL_MODE", "lines")
    if mode == "chars":
        return tail_chars(lines, n)
    return tail_lines(lines, n)


def main():
    if args.file:
        if args.follow:
            follow_file(args.file, args.lines)
        else:
            lines = read_file(args.file)
            for line in handle_mode(lines, args.lines):
                print(line, end="")
    else:
        input_lines = sys.stdin.readlines()
        for line in handle_mode(input_lines, args.lines):
            print(line, end="")


if __name__ == "__main__":
    main()
