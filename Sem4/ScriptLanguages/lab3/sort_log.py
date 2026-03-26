def sort_log(log, index):
    try:
        return sorted(log, key=lambda x: x[index])
    except IndexError:
        print("Wrong index")
        return log

