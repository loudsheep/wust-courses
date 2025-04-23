import subprocess
import sys
import os
from pathlib import Path
import difflib

def run_test(program_path, input_path):
    """Uruchamia program i zwraca (exit_code, stdout, stderr)"""
    try:
        result = subprocess.run(
            ['python', program_path],  # Można rozbudować o obsługę innych języków
            input=input_path.read_text(),
            text=True,
            capture_output=True,
            timeout=50
        )
        return result.returncode, result.stdout, result.stderr
    except Exception as e:
        return -1, "", str(e)

def compare_outputs(actual_output, expected_output):
    """Zwraca None jeśli są takie same, albo listę różnic"""
    actual_lines = actual_output.strip().splitlines()
    expected_lines = expected_output.strip().splitlines()
    diff = list(difflib.unified_diff(expected_lines, actual_lines, lineterm=''))
    return diff if diff else None

def main(program_path, tests_folder):
    program_path = Path(program_path)
    tests_folder = Path(tests_folder)

    test_files = sorted(tests_folder.glob('*.in'))
    print(f"Znaleziono {len(test_files)} testów.\n")

    for test_in in test_files:
        test_out = test_in.with_suffix('.out')
        if not test_out.exists():
            print(f"[POMINIĘTO] {test_in.name} - brak pliku {test_out.name}")
            continue

        print(f"[TEST] {test_in.name}")
        exit_code, stdout, stderr = run_test(program_path, test_in)

        if exit_code != 0:
            print(f" ❌ Program zakończył się błędem:\n{stderr.strip()}\n")
            continue

        expected_output = test_out.read_text()
        differences = compare_outputs(stdout, expected_output)

        if differences:
            print(" ❌ Niepoprawny wynik. Różnice:")
            for line in differences:
                print(line)
            print()
        else:
            print(" ✅ Test przeszedł pomyślnie.\n")

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("Użycie: python tester.py <ścieżka_do_programu> <ścieżka_do_folderu_z_testami>")
        sys.exit(1)

    main(sys.argv[1], sys.argv[2])
