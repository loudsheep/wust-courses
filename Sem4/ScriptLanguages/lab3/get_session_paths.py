def get_session_paths(log):
    session_paths = {}
    for entry in log:
        uid = entry[1]
        uri = entry[8]
        if uid not in session_paths:
            session_paths[uid] = []
        session_paths[uid].append(uri)
    return session_paths


def main():
    from read_log import read_log

    log = read_log()
    session_paths = get_session_paths(log)
    for uid, paths in session_paths.items():
        print(f"UID: {uid}, Paths: {paths}")


if __name__ == "__main__":
    main()
