import os
import sys

def list_dirs():
    for d in os.environ.get("PATH","").split(os.pathsep):
        print(d)

def list_dirs_and_files():
    for d in os.environ.get("PATH","").split(os.pathsep):
        print(d)
        if os.path.isdir(d):
            for f in os.listdir(d):
                full_path = os.path.join(d, f)
                if os.path.isfile(full_path) and os.access(full_path, os.X_OK):
                    print(f"\t{f}")


def path_operations():
    if len(sys.argv) != 2:
        print("Usage of script:\n\t--dirs - show directories\n\t--execs - executables in folders")
        return
    if sys.argv[1] == "--dirs":
        list_dirs()
    elif sys.argv[1] == "--execs":
        list_dirs_and_files()
    else:
        print("Invaid argument")
        print("Usage of script:\n\t--dirs - show directories\n\t--execs - executables in folders")

if __name__ == "__main__":
    path_operations()
