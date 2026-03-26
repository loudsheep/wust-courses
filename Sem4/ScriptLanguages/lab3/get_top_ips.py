from collections import Counter

def get_top_ips(log, n=10):
    ips = [e[2] for e in log]
    return Counter(ips).most_common(n)
