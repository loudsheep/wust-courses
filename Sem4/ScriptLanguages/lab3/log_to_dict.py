from entry_to_dict import entry_to_dict


def log_to_dict(log):
    log_dict = {}
    for entry in log:
        uid = entry[1]
        entry_dict = entry_to_dict(entry)
        if uid not in log_dict:
            log_dict[uid] = []
        log_dict[uid].append(entry_dict)
    return log_dict


def main():
    from read_log import read_log

    log = read_log()
    log_dict = log_to_dict(log)

    for uid, entries in log_dict.items():
        print(f"UID: {uid}")
        for entry in entries:
            print(entry)


if __name__ == "__main__":
    main()
