def print_dict_entry_dates(log_dict):
    for uid, entries in log_dict.items():
        ip_hosts = set()
        request_count = len(entries)
        first_date = entries[0]["timestamp"]
        last_date = entries[-1]["timestamp"]
        method_counts = {}
        status_code_counts = {"2xx": 0, "total": 0}

        for entry in entries:
            ip_hosts.add(entry["id_orig_h"])
            method = entry["method"]
            method_counts[method] = method_counts.get(method, 0) + 1
            status_code = entry["status_code"]
            if status_code is not None:
                status_code_counts["total"] += 1
                if 200 <= status_code < 300:
                    status_code_counts["2xx"] += 1

        print(f"UID: {uid}")
        print(f"IP/Host: {', '.join(ip_hosts)}")
        print(f"Number of requests: {request_count}")
        print(f"First request date: {first_date}")
        print(f"Last request date: {last_date}")
        print("HTTP method distribution:")
        for method, count in method_counts.items():
            percentage = (count / request_count) * 100
            print(f" - {method}: {percentage:.2f}%")
        if status_code_counts["total"] > 0:
            ratio_2xx = (status_code_counts["2xx"] / status_code_counts["total"]) * 100
            print(f"Ratio of 2xx codes: {ratio_2xx:.2f}%")
        else:
            print("No status codes available.")
        print()


def main():
    from log_to_dict import log_to_dict
    from read_log import read_log

    log = read_log()
    log_dict = log_to_dict(log)

    # for testing
    log_dict = {uid: entries for uid, entries in log_dict.items() if len(entries) >= 100}

    print_dict_entry_dates(log_dict)


if __name__ == "__main__":
    main()
