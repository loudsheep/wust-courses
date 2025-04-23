import subprocess
import sys
import os
from pathlib import Path
import difflib
import tempfile
import shutil

def run_python(program_path, input_text):
    result = subprocess.run(
        ['python', str(program_path)],
        input=input_text,
        text=True,
        capture_output=True,
        timeout=10
    )
    return result.returncode, result.stdout, result.stderr

def run_java(program_path, input_text):
    source_dir = program_path.parent
    class_name = program_path.stem

    # Tworzymy tymczasowy folder na pliki .class
    with tempfile.TemporaryDirectory() as build_dir:
        # Kompilujemy wszystkie pliki .java do build_dir
        java_files = list(source_dir.rglob("*.java"))
        compile_result = subprocess.run(
            ['javac', '-d', build_dir] + [str(file) for file in java_files],
            capture_output=True,
            text=True
        )

        if compile_result.returncode != 0:
            return compile_result.returncode, "", compile_result.stderr

        # Wyznaczamy nazwę klasy głównej z uwzględnieniem pakietu
        relative_path = program_path.with_suffix('').relative_to(source_dir.parent)
        fully_qualified_class = ".".join(relative_path.parts)

        # Uruchamiamy program z tymczasowego build_dir
        run_result = subprocess.run(
            ['java', '-cp', build_dir, fully_qualified_class],
            input=input_text,
            text=True,
            capture_output=True,
            timeout=10
        )

        return run_result.returncode, run_result.stdout, run_result.stderr


def run_test(program_path, input_path):
    try:
        input_text = input_path.read_text()
        if program_path.suffix == '.py':
            return run_python(program_path, input_text)
        elif program_path.suffix == '.java':
            return run_java(program_path, input_text)
        else:
            return -1, "", f"Nieobsługiwany typ pliku: {program_path.suffix}"
    except Exception as e:
        return -1, "", str(e)

def compare_outputs(actual_output, expected_output):
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
        print("Użycie: python test_runner.py <ścieżka_do_programu> <ścieżka_do_folderu_z_testami>")
        sys.exit(1)

    main(sys.argv[1], sys.argv[2])
