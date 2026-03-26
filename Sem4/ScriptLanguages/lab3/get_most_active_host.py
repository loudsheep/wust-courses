

def get_most_active_host(log):
    host_reqs = {}
    for entry in log:
        ts = entry[0]
        host = entry[2]

        if host in host_reqs:
            host_reqs[host].append(ts)
        else:
            host_reqs[host] = [ts]

    max_host = None
    max_sum = 0
    for host, reqs in host_reqs.items():
        if len(reqs) < 2:
            continue
        s = 0
        for i in range(len(reqs) - 1):
            s += (reqs[i + 1] - reqs[i]).total_seconds()
        if s > max_sum:
            max_sum = s
            max_host = host
    
    return max_host, max_sum


def main():
    from read_log import read_log

    log = read_log()
    h, s = get_most_active_host(log)
    print(f"most active host: {h} total time: {s}")


if __name__ == "__main__":
    main()

