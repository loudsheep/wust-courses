import re

def is_valid_ip(addr):
    if not isinstance(addr, str):
        return False
    pattern=r"^\d{1,3}(\.\d{1,3}){3}$"
    return re.match(pattern, addr)

def get_entries_by_addr(log, addr):
    if not is_valid_ip(addr):
        raise ValueError("Invalid address")
    return [e for e in log if e[2] == addr or e[7] == addr]

