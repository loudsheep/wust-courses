# 🎓 University Code Repository

Welcome to my personal repository where I store all coding assignments and helper scripts related to my studies at **Wrocław University of Science and Technology**, where I study **Applied Computer Science**.

This repository is organized by semesters and courses to maintain clarity and scalability as I progress through my studies.

---

## 📁 Repository Structure

```
.
├── Sem1/
│   ├── CourseA/
│   │   ├── Task1/
│   │   └── Task2/
│   └── CourseB/
│       └── Task1/
├── Sem2/
│   └── ...
├── Util/
│   └── tester.py
```

- `SemX/`: Contains course-related code for semester X.
- `CourseName/`: Inside each semester folder, holds tasks for a specific course.
- `TaskName/`: Contains the files related to a specific assignment/task.
- `Util/`: Special folder for utility scripts I've written to support or automate coding tasks.

---

## 🧪 `tester.py` - Algorithm Testing Utility

A Python script designed to automate the testing of programs using predefined input/output files.

> **Supported Languages:** Currently supports testing of programs written in:
> - Python (`.py`)
> - Java (`.java`)
> - C++ (`.cpp`)

### 🔧 How to Use

```bash
python tester.py <program_path> <tests_folder> [--timeout=N]
```

### 📥 Arguments

- `<program_path>` – Path to the program you want to test (can be executable or script).
- `<tests_folder>` – Path to the folder containing test files.
- `[--timeout=N]` – *(Optional)* Sets a timeout (in seconds) after which a test is considered failed.

### 📁 Test Folder Format

The test directory should include pairs of files:

- `test_name.in` – Input file for the test.
- `test_name.out` – Expected output file.

Each `.in` file will be piped into the tested program and the program's output will be compared to the corresponding `.out` file.

---

## ✅ Goals

- Maintain a clean and scalable structure for university code.
- Automate common development and testing tasks using custom utilities.
- Improve and expand this repository throughout my academic journey.

---

Feel free to explore, fork, or contribute if you find something useful or interesting!
