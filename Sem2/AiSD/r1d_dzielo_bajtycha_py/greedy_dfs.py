import sys
sys.setrecursionlimit(1_000_000)

n, k = map(int, input().split())
values = [0] + list(map(int, input().split()))  # indeks od 1 dla wygody

tree = [[] for _ in range(n + 1)]
for i in range(2, n + 1):
    parent = int(input())
    tree[parent].append(i)

in_time = [-1] * (n + 1)
out_time = [-1] * (n + 1)
current_time = 0

# DFS do obliczenia czasów wejścia/wyjścia i zebrania wartości liści
def dfs(node):
    global current_time
    in_time[node] = current_time
    current_time += 1
    
    for child in tree[node]:
        dfs(child)
    
    out_time[node] = current_time
    current_time += 1

# Wykonaj DFS od korzenia
dfs(1)

# Zbiór obrazów z czasami wejścia/wyjścia
paintings = []
for i in range(1, n + 1):
    paintings.append((values[i], in_time[i], out_time[i], i))

# Sortujemy obrazy po wartości malejąco, aby zacząć od najcenniejszych
paintings.sort(reverse=True, key=lambda x: x[0])

# Zbiór wybranych obrazów
selected = set()
total_value = 0
count = 0

# Greedy wybór obrazów
for value, start, end, idx in paintings:
    # Sprawdzamy, czy obraz jest niezależny (nie jest przodkiem żadnego już wybranego obrazu)
    can_select = True
    for s in selected:
        if in_time[s] <= start <= out_time[s] or in_time[s] <= end <= out_time[s]:
            can_select = False
            break
    
    if can_select:
        selected.add(idx)
        total_value += value
        count += 1
    
    # Zatrzymujemy, gdy wybraliśmy już k obrazów
    if count == k:
        break

# Wynik
if count == k:
    print(total_value)
else:
    print(-1)
