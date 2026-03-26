def get_entries_in_time_range(log, start, end):
    return [e for e in log if start <= e[0] < end]
