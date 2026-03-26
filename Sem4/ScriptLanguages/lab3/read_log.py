import sys
from datetime import datetime

def read_log():
    log = []
    i=0
    for line in sys.stdin:
        line == line.strip()
        if line == "":
            continue
        elements = line.split("\t")
        ts = datetime.fromtimestamp(float(elements[0]))
        uid = elements[1]
        id_orig_h = elements[2]
        id_orig_p = elements[3]
        id_resp_h = elements[4]
        id_resp_p = elements[5]
        method = elements[7]
        host = elements[8]
        uri = elements[9]
        status_code = int(elements[14]) if elements[14].isdigit() else None
        entry = (ts, uid, id_orig_h, id_orig_p, id_resp_h, id_resp_p, method, host, uri, status_code)
        i+=1
        log.append(entry)

    return log

if __name__ == "__main__":
    print(read_log())
