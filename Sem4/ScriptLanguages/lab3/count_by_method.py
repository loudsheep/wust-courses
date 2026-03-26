def count_by_method(log):
    result = {}

    for e in log:
        method = e[6]
        result[method] = result.get(method, 0) + 1

    return result
