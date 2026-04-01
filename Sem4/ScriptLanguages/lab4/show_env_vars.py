import os
import sys

def show_env_vars():
    env_vars = os.environ
    filters = [arg.lower() for arg in sys.argv[1:]]

    if filters:
        filtered = {k: v for k, v in env_vars.items()
                    if any(f in k.lower() for f in filters)}
    else:
        filtered = env_vars

    for key in sorted(filtered.keys(), key=str.lower):
        print(f"{key}={filtered[key]}")

if __name__ == "__main__":
    show_env_vars()
