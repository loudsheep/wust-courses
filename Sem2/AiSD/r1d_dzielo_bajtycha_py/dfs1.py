import sys
sys.setrecursionlimit(1_000_000)

n, k = map(int, input().split())
values = [0] + list(map(int, input().split()))  # index od 1 dla wygody

tree = [[] for _ in range(n + 1)]
for i in range(2, n + 1):
    parent = int(input())
    tree[parent].append(i)

# dp[node][i] = maksymalna wartość zbioru i różnych obrazów z poddrzewa node
dp = [ [float('-inf')] * (k + 1) for _ in range(n + 1) ]

def dfs(node):
    dp[node][0] = 0  # nie wybieramy nic
    if k >= 1:
        dp[node][1] = values[node]  # wybieramy tylko ten obraz

    for child in tree[node]:
        dfs(child)

        # tymczasowa tablica do łączenia wyników
        temp = [float('-inf')] * (k + 1)
        for i in range(k + 1):
            if dp[node][i] == float('-inf'):
                continue
            for j in range(k + 1 - i):
                if dp[child][j] == float('-inf'):
                    continue
                temp[i + j] = max(temp[i + j], dp[node][i] + dp[child][j])

        dp[node] = temp

dfs(1)

result = dp[1][k]
print(-1 if result == float('-inf') else result)