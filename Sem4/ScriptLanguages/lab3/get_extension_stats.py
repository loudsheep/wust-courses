def get_extension_stats(log):
    extension_stats = {}
    for entry in log:
        uri = entry[8]
        if "?" in uri:
            uri = uri.split("?", 1)[0]
        if "." in uri:
            extension = uri.rsplit(".", 1)[
                -1
            ].lower()
            if extension.isalnum():
                extension_stats[extension] = extension_stats.get(extension, 0) + 1
    return extension_stats


def main():
    from read_log import read_log

    log = read_log()[:-10]
    stats = get_extension_stats(log)
    print("Extension statistics:")
    for ext, count in stats.items():
        print(f"{ext}: {count}")


if __name__ == "__main__":
    main()
