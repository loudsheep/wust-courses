def get_failed_reads(log, merge=False):
    errors_4xx = [e for e in log if e[9] is not None and 400 <= e[9] < 500]
    errors_5xx = [e for e in log if e[9] is not None and 500 <= e[9] < 600]
    if merge:
        return errors_4xx + errors_5xx
    return errors_4xx, errors_5xx

