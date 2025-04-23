import sys

sys.setrecursionlimit(1_000_000)

n, k = map(int, input().split())
values = [0] + list(map(int, input().split()))

tree = [[] for _ in range(n + 1)]
for i in range(2, n + 1):
    parent = int(input())
    tree[parent].append(i)

dp = [[float("-inf")] * (k + 1) for _ in range(n + 1)]  # wybrany z korzeniem
ndp = [[float("-inf")] * (k + 1) for _ in range(n + 1)]  # bez


def dfs(node):
    dp[node][1] = values[node]  # wybieramy tylko ten wierzchołek
    ndp[node][0] = 0  # nie wybieramy aktualnego

    for child in tree[node]:
        dfs(child)

        # tmpp tablice na wyniki
        new_dp = [float("-inf")] * (k + 1)
        new_ndp = [float("-inf")] * (k + 1)

        for i in range(k + 1):
            if ndp[node][i] == float("-inf"):
                continue
            for j in range(k + 1 - i):
                best = max(dp[child][j], ndp[child][j])
                if best != float("-inf"):
                    new_ndp[i + j] = max(new_ndp[i + j], ndp[node][i] + best)

        for i in range(k + 1):
            if dp[node][i] == float("-inf"):
                continue
            new_dp[i] = dp[node][i]

        dp[node] = new_dp
        ndp[node] = new_ndp


dfs(1)

result = max(dp[1][k], ndp[1][k])
print(-1 if result == float("-inf") else result)
