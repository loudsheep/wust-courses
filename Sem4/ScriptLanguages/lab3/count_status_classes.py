from collections import Counter


def count_status_classes(log):
    status_classes = []
    for entry in log:
        status_code = entry[9]
        if status_code is not None:
            status_class = f"{status_code // 100}xx"
            status_classes.append(status_class)
    return dict(Counter(status_classes))


def main():
    from read_log import read_log

    log = read_log()
    status_class_counts = count_status_classes(log)
    for status_class, count in status_class_counts.items():
        print(f"{status_class}: {count}")


if __name__ == "__main__":
    main()
