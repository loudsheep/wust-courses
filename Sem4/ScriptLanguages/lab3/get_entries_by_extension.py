def get_entries_by_extension(log, ext):
    result = []

    for e in log:
        uri = e[8].split("?")[0]
        if uri.endswith("." + ext):
            result.append(e)

    return result
