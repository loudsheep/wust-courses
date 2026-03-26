def analyze_log(log):
    from collections import Counter

    ip_counter = Counter()
    uri_counter = Counter()
    method_counter = Counter()
    error_count = 0
    unique_uids = set()
    unique_ports = set()

    for entry in log:
        ip_counter[entry[2]] += 1
        uri_counter[entry[8]] += 1
        method_counter[entry[6]] += 1
        if entry[9] is not None and entry[9] >= 400:
            error_count += 1
        unique_uids.add(entry[1])
        unique_ports.add(entry[5])

    most_common_ip = ip_counter.most_common(1)[0][0] if ip_counter else None
    most_common_uri = uri_counter.most_common(1)[0][0] if uri_counter else None

    return {
        "most_common_ip": most_common_ip,
        "most_common_uri": most_common_uri,
        "method_distribution": dict(method_counter),
        "error_count": error_count,
        "unique_user_count": len(unique_uids),
        "unique_response_ports": unique_ports,
    }


def main():
    from read_log import read_log

    log = read_log()
    analysis = analyze_log(log)
    print(analysis)


if __name__ == "__main__":
    main()
