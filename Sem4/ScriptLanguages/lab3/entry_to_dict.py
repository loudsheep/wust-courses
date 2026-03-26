def entry_to_dict(entry):
    keys = [
        "timestamp",
        "uid",
        "id_orig_h",
        "id_orig_p",
        "id_resp_h",
        "id_resp_p",
        "method",
        "host",
        "uri",
        "status_code",
    ]
    return dict(zip(keys, entry))


def main():
    from read_log import read_log

    log = read_log()
    dict_entries = [entry_to_dict(e) for e in log]
    for d in dict_entries:
        print(d)


if __name__ == "__main__":
    main()
