import sys
from sys import stdin
from collections import deque

def main():
    sys.setrecursionlimit(1 << 25)
    n, k = map(int, stdin.readline().split())
    a = list(map(int, stdin.readline().split()))
    parent = [0] * (n - 1)
    for i in range(n - 1):
        parent[i] = int(stdin.readline()) - 1  # 0-based

    # Build the tree
    children = [[] for _ in range(n)]
    for i in range(n - 1):
        p = parent[i]
        children[p].append(i + 1)  # node i+1 has parent p

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
    dp_sel = [ [-float('inf')] * (k + 1) for _ in range(n) ]
    dp_not_sel = [ [-float('inf')] * (k + 1) for _ in range(n) ]

    for u in post_order:
        # Initialize dp_sel
        dp_sel[u][1] = a[u]
        for m in range(2, k + 1):
            dp_sel[u][m] = -float('inf')

        # Initialize dp_not_sel: start with 0 for m=0
        dp_not_sel[u][0] = 0
        for child in children[u]:
            # Create best_v: max between dp_sel[child] and dp_not_sel[child]
            best_v = [ max(dp_sel[child][s], dp_not_sel[child][s]) for s in range(k + 1) ]

            # Merge best_v into dp_not_sel[u] using knapsack
            temp = [-float('inf')] * (k + 1)
            for t in range(k + 1):
                if dp_not_sel[u][t] == -float('inf'):
                    continue
                for s in range(k + 1 - t):
                    if best_v[s] == -float('inf'):
                        continue
                    if temp[t + s] < dp_not_sel[u][t] + best_v[s]:
                        temp[t + s] = dp_not_sel[u][t] + best_v[s]
            # Update dp_not_sel[u]
            for m in range(k + 1):
                if temp[m] > dp_not_sel[u][m]:
                    dp_not_sel[u][m] = temp[m]

        # Ensure dp_not_sel[u] doesn't have better values for m=0
        dp_not_sel[u][0] = 0

    # The root is node 0 (0-based)
    max_sum = max(dp_sel[0][k], dp_not_sel[0][k])
    if max_sum < 0:
        print(-1)
    else:
        print(max_sum)

if __name__ == "__main__":
    main()
