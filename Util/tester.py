import subprocess
import sys
import os
from pathlib import Path
import difflib
import tempfile
import time

# ANSI colors
RED = "\033[91m"
GREEN = "\033[92m"
YELLOW = "\033[93m"
BLUE = "\033[94m"
CYAN = "\033[96m"
BOLD = "\033[1m"
RESET = "\033[0m"


def run_python(program_path, input_text, timeout):
    result = subprocess.run(
        ['python', str(program_path)],
        input=input_text,
        text=True,
        capture_output=True,
        timeout=timeout
    )
    return result.returncode, result.stdout, result.stderr


def run_java(program_path, input_text, timeout):
    source_dir = program_path.parent
    class_name = program_path.stem

    with tempfile.TemporaryDirectory() as build_dir:
        java_files = list(source_dir.rglob("*.java"))
        compile_result = subprocess.run(
            ['javac', '-d', build_dir] + [str(file) for file in java_files],
            capture_output=True,
            text=True
        )

        if compile_result.returncode != 0:
            return compile_result.returncode, "", compile_result.stderr

        relative_path = program_path.with_suffix('').relative_to(source_dir.parent)
        if len(relative_path.parts) > 0 and relative_path.parts[0] == "src":
            parts = relative_path.parts[1:]
        else:
            parts = relative_path.parts
        fully_qualified_class = ".".join(parts)

        run_result = subprocess.run(
            ['java', '-cp', build_dir, fully_qualified_class],
            input=input_text,
            text=True,
            capture_output=True,
            timeout=timeout
        )

        return run_result.returncode, run_result.stdout, run_result.stderr


def run_test(program_path, input_path, timeout):
    try:
        input_text = input_path.read_text()
        if program_path.suffix == '.py':
            return run_python(program_path, input_text, timeout)
        elif program_path.suffix == '.java':
            return run_java(program_path, input_text, timeout)
        else:
            return -1, "", f"Unsupported file type: {program_path.suffix}"
    except Exception as e:
        return -1, "", str(e)


def compare_outputs(actual_output, expected_output):
    actual_lines = actual_output.strip().splitlines()
    expected_lines = expected_output.strip().splitlines()
    diff = list(difflib.unified_diff(expected_lines, actual_lines, lineterm=''))
    return diff if diff else None


def main(program_path, tests_folder, timeout=10):
    program_path = Path(program_path)
    tests_folder = Path(tests_folder)

    test_files = sorted(tests_folder.glob('*.in'))
    print(f"{CYAN}Found {len(test_files)} test(s).{RESET}\n")

    passed_count = 0
    failed_count = 0
    skipped_count = 0

    for test_in in test_files:
        test_out = test_in.with_suffix('.out')
        if not test_out.exists():
            print(f"{YELLOW}[SKIPPED]{RESET} {test_in.name} - missing output file: {test_out.name}")
            skipped_count += 1
            continue

        print(f"{CYAN}[TEST] {test_in.name}{RESET}")
        start_time = time.perf_counter()
        exit_code, stdout, stderr = run_test(program_path, test_in, timeout)
        duration = time.perf_counter() - start_time

        if exit_code != 0:
            print(f"{RED} ❌ Program exited with an error ({duration:.2f}s):\n{stderr.strip()}{RESET}\n")

            failed_count += 1
            continue

        expected_output = test_out.read_text()
        differences = compare_outputs(stdout, expected_output)

        if differences:
            print(f"{RED} ❌ Test failed{RESET} ({duration:.2f}s). Differences:")
            for line in differences:
                if line.startswith('+'):
                    print(f"{GREEN}{line}{RESET}")
                elif line.startswith('-'):
                    print(f"{RED}{line}{RESET}")
                elif line.startswith('@@'):
                    print(f"{YELLOW}{line}{RESET}")
                else:
                    print(line)
            print()
            failed_count += 1
        else:
            print(f"{GREEN} ✅ Test passed successfully{RESET} ({duration:.2f}s)\n")
            passed_count += 1

    # 🧾 Summary
    print(BOLD + "\nTest Summary:" + RESET)
    print(f"{GREEN}  Passed:  {passed_count}{RESET}")
    print(f"{RED}  Failed:  {failed_count}{RESET}")
    print(f"{YELLOW}  Skipped: {skipped_count}{RESET}")
    print(BOLD + f"\nTotal run: {passed_count + failed_count}, of {len(test_files)} total.{RESET}")


if __name__ == '__main__':
    if len(sys.argv) < 3 or len(sys.argv) > 4:
        print(f"{YELLOW}Usage:{RESET} python test_runner.py <program_path> <tests_folder> [--timeout=N]")
        sys.exit(1)

    program_path = sys.argv[1]
    tests_folder = sys.argv[2]
    timeout = 10  # default

    if len(sys.argv) == 4:
        try:
            if sys.argv[3].startswith('--timeout='):
                timeout = float(sys.argv[3].split('=')[1])
        except ValueError:
            print(f"{RED}Invalid timeout value. Must be a number.{RESET}")
            sys.exit(1)

    main(program_path, tests_folder, timeout)
