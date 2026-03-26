def detect_sus(log, threshold):
    ip_counts = {}
    for entry in log:
        ip = entry[2]
        status_code = entry[9]
        if ip not in ip_counts:
            ip_counts[ip] = {"total": 0, "404": 0}
        ip_counts[ip]["total"] += 1
        if status_code == 404:
            ip_counts[ip]["404"] += 1

    suspicious_ips = []
    for ip, counts in ip_counts.items():
        if counts["total"] >= threshold or (counts["404"] / counts["total"]) > 0.5:
            suspicious_ips.append(ip)

    return suspicious_ips


def main():
    from read_log import read_log

    log = read_log()
    threshold = 100
    suspicious_ips = detect_sus(log, threshold)
    print("Suspicious IPs:")
    for ip in suspicious_ips:
        print(ip)


if __name__ == "__main__":
    main()
