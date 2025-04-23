import sys
sys.setrecursionlimit(1_000_000)

n, k = map(int, input().split())
values = [0] + list(map(int, input().split()))  # Indeks od 1 dla wygody

tree = [[] for _ in range(n + 1)]
for i in range(2, n + 1):
    parent = int(input())
    tree[parent].append(i)

# DP: dp[node][i] = maksymalna wartość, gdy wybrano i obrazów z poddrzewa node
dp = [[float('-inf')] * (k + 1) for _ in range(n + 1)]  # DP dla węzła
dp[1][0] = 0  # Jeśli nie wybierzemy żadnego obrazu z korzenia, to wartość wynosi 0

# DFS
def dfs(node):
    dp[node][0] = 0  # Zawsze możemy wybrać 0 obrazów za 0 złota
    dp[node][1] = values[node]  # Możemy wybrać tylko ten obraz, jeśli k >= 1

    for child in tree[node]:
        dfs(child)

        # Tymczasowa tablica do merge'owania wyników
        temp = [float('-inf')] * (k + 1)
        
        # Złączanie podzbiorów obrazów dla node i child
        for i in range(k + 1):  # Liczba obrazów wybranych z node
            if dp[node][i] == float('-inf'):
                continue
            for j in range(k + 1 - i):  # Liczba obrazów wybranych z dziecka
                if dp[child][j] == float('-inf'):
                    continue
                temp[i + j] = max(temp[i + j], dp[node][i] + dp[child][j])

        # Zaktualizuj dp[node]
        dp[node] = temp

# Wykonaj DFS od korzenia
dfs(1)

# Wynik
result = dp[1][k]
print(-1 if result == float('-inf') else result)
