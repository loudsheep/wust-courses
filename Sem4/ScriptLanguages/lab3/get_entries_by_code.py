def get_entries_by_code(log, code):
    if not isinstance(code, int):
        raise ValueError("Code has to be int")
    return [e for e in log if e[9] == code]

