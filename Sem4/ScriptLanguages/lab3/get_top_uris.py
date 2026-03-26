from collections import Counter


def get_top_uris(log, n=10):
    uris = [e[8] for e in log]
    return Counter(uris).most_common(n)


def main():
    from read_log import read_log

    log = read_log()
    top_uris = get_top_uris(log)
    for uri, count in top_uris:
        print(f"{uri}: {count}")


if __name__ == "__main__":
    main()
