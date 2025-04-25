import sys
import numpy as np
from sys import stdin

def main():
    sys.setrecursionlimit(1 << 25)
    n, k = map(int, stdin.readline().split())
    a = np.fromstring(stdin.readline(), dtype=int, sep=' ')
    parent = np.empty(n - 1, dtype=int)
    for i in range(n - 1):
        parent[i] = int(stdin.readline()) - 1  # 0-based

    # Build the tree
    children = [[] for _ in range(n)]
    for i in range(n - 1):
        p = parent[i]
        children[p].append(i + 1)

    # Post-order traversal
    post_order = []
    stack = [(0, False)]
    while stack:
        node, visited = stack.pop()
        if visited:
            post_order.append(node)
            continue
        stack.append((node, True))
        for child in reversed(children[node]):
            stack.append((child, False))

    # Initialize DP arrays
    dp_sel = np.full((n, k + 1), -np.inf)
    dp_not_sel = np.full((n, k + 1), -np.inf)

    for u in post_order:
        dp_sel[u][1] = a[u]  # Select node u with 1 selection
        dp_not_sel[u][0] = 0  # Not selecting u, 0 items selected

        for child in children[u]:
            best_v = np.maximum(dp_sel[child], dp_not_sel[child])
            temp = np.full(k + 1, -np.inf)

            for t in range(k + 1):
                if dp_not_sel[u][t] == -np.inf:
                    continue
                max_s = k - t
                for s in range(max_s + 1):
                    if best_v[s] == -np.inf:
                        continue
                    temp[t + s] = max(temp[t + s], dp_not_sel[u][t] + best_v[s])

            dp_not_sel[u] = np.maximum(dp_not_sel[u], temp)
            dp_not_sel[u][0] = 0  # Ensure consistency for 0 selections

    max_sum = max(dp_sel[0][k], dp_not_sel[0][k])
    print(-1 if max_sum < 0 else int(max_sum))

if __name__ == "__main__":
    main()
