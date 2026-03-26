
def find_longest_session(log_dict):
    max_session_length = 0
    longest_session_uid = None

    for uid, entries in log_dict.items():
        session_length = len(entries)
        if session_length > max_session_length:
            max_session_length = session_length
            longest_session_uid = uid

    return longest_session_uid, max_session_length


def main():
    from log_to_dict import log_to_dict
    from read_log import read_log

    log = read_log()
    log_dict = log_to_dict(log)

    uid, num = find_longest_session(log_dict)
    print(f"Longest session UID: {uid}, Length: {num}")

if __name__ == "__main__":
    main()