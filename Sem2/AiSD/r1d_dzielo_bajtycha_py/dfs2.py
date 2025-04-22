######################### Wejście
n, k = map(int, input().split())
values = [0] + list(map(int, input().split()))  # indeksujemy od 1

tree = [[] for _ in range(n + 1)]
for i in range(2, n + 1):
    parent = int(input())
    tree[parent].append(i)
    
######################### Obliczenia

