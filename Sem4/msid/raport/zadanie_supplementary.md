Politechnika Wrocławska

Wydział Informatyki i Telekomunikacji

Metody Systemowe i Decyzyjne

**Projekt semestralny – materiały dodatkowe**

---

## Zastosowanie metaheurystyk w problemie harmonogramowania produkcji

1. Opis problemu decyzyjnego

Rozważany jest problem harmonogramowania produkcji typu Permutation Flow Shop Scheduling Problem (PFSP). Dany jest system produkcyjny składający się z:

- $N$ zadań (jobs),

- $M$ maszyn.

Każde zadanie musi zostać wykonane na wszystkich maszynach w tej samej kolejności technologicznej:

$$M_1 \to M_2 \to \dots \to M_M$$

Nie dopuszcza się przerwania realizacji zadania na maszynie.

1. Dane wejściowe

Dane problemu określa macierz czasów przetwarzania:

$$P = [p_{j,m}]$$

gdzie:

- $p_{j,m}$ — czas przetwarzania zadania $j$ na maszynie $m$.

Macierz ma wymiar $N \times M$.
Przykład macierzy czasów:

$$P = \begin{bmatrix} 5 & 2 & 6 \\ 4 & 7 & 3 \\ 6 & 3 & 5 \end{bmatrix}$$

| Zadanie | M1  | M2  | M3  |
| :------ | :-- | :-- | :-- |
| J1      | 5   | 2   | 6   |
| J2      | 4   | 7   | 3   |
| J3      | 6   | 3   | 5   |

1. Zmienne decyzyjne

Rozwiązanie problemu stanowi permutacja zadań:

$$\pi = (\pi_1, \pi_2, \dots, \pi_N)$$

która określa kolejność realizacji zadań.

Interpretacja permutacji:
Jeżeli $\pi = (3, 1, 2)$ , to zadania wykonywane będą w kolejności:

1. zadanie 3

2. zadanie 1

3. zadanie 2

4. Funkcja celu

Celem optymalizacji jest minimalizacja czasu zakończenia wszystkich zadań (makespan):

$$C_{max}$$

czyli czasu zakończenia ostatniego zadania na ostatniej maszynie.

1. Obliczanie czasu realizacji harmonogramu

Czas zakończenia zadania na pozycji $i$ harmonogramu na maszynie $m$ oznaczamy jako:

$$C(i,m)$$

Obliczamy go ze wzoru:

$$C(i,m) = \max(C(i-1,m), C(i,m-1)) + p_{\pi_i, m}$$

1. Objaśnienie oznaczeń

- $i$ — pozycja zadania w harmonogramie

- $m$ — numer maszyny

- $C(i,m)$ — czas zakończenia zadania

- $\pi_i$ — numer zadania na pozycji $i$

- $p_{j,m}$ — czas przetwarzania zadania $j$ na maszynie $m$

Zapis $p_{\pi_i, m}$ oznacza czas przetwarzania zadania znajdującego się na pozycji $i$ harmonogramu na maszynie $m$.

1. Wartość funkcji celu

Ostateczna wartość funkcji celu:

$$C_{max} = C(N,M)$$

1. Algorytmy optymalizacji

W projekcie należy zaimplementować następujące algorytmy metaheurystyczne:

- Algorytm genetyczny (Genetic Algorithm)

- Symulowane wyżarzanie (Simulated Annealing)

- Algorytm kolonii mrówek (Ant Colony Optimization)

- Algorytm pszczeli (Bees Algorithm)

---

1. Algorytm symulowanego wyżarzania

Algorytm Simulated Annealing (SA) polega na iteracyjnym przeszukiwaniu przestrzeni rozwiązań. W każdej iteracji:

- generowane jest nowe rozwiązanie,

- obliczana jest funkcja celu,

- podejmowana jest decyzja o jego akceptacji.

1. Dlaczego dopuszczamy gorsze rozwiązania

W wielu problemach optymalizacji istnieje wiele minimów lokalnych. Algorytm, który akceptuje wyłącznie rozwiązania lepsze, może zatrzymać się w minimum lokalnym. Dlatego w algorytmie symulowanego wyżarzania dopuszcza się czasami akceptację rozwiązań gorszych, aby umożliwić dalsze przeszukiwanie przestrzeni rozwiązań.

1. Kryterium akceptacji rozwiązania

Niech:

- $x_{current}$ — aktualne rozwiązanie

- $x_{new}$ — nowe rozwiązanie

Funkcja celu:

$$f(x) = C_{max}$$

Zmiana funkcji celu:

$$\Delta = f(x_{new}) - f(x_{current})$$

czyli:

$$\Delta = C_{max}^{new} - C_{max}^{current}$$

1. Interpretacja wartości $\Delta$

Jeżeli $\Delta < 0$ , nowe rozwiązanie jest lepsze i zostaje zaakceptowane.
Jeżeli $\Delta > 0$ , rozwiązanie jest gorsze i może zostać zaakceptowane z pewnym prawdopodobieństwem.

1. Prawdopodobieństwo akceptacji

$$P = e^{-\frac{\Delta}{T}}$$

gdzie:

- $T$ — temperatura algorytmu.

1. Implementacja w MATLAB

```matlab
delta = newCost - currentCost; [cite_start]% [cite: 915]
[cite_start]if delta <= 0 % [cite: 916]
    accept = true; [cite_start]% [cite: 917]
[cite_start]else % [cite: 918]
    P = exp(-delta/T); [cite_start]% [cite: 919]
    accept = rand < P; [cite_start]% [cite: 920]
end %

```

1. Generowanie sąsiedztwa dla permutacji

W algorytmach optymalizacyjnych konieczne jest generowanie nowych rozwiązań w pobliżu aktualnego rozwiązania. Typowe operatory dla permutacji:

- **Swap**: zamiana dwóch zadań `[1 2 3 4 5]` $\to$ `[1 4 3 2 5]`

- **Insert**: przeniesienie elementu w inne miejsce `[1 2 3 4 5]` $\to$ `[1 3 4 2 5]`

- **Inversion**: odwrócenie fragmentu permutacji `[1 2 3 4 5]` $\to$ `[1 4 3 2 5]`

---

1. Sugestie implementacyjne — Algorytm genetyczny

**Reprezentacja rozwiązania:**

- pojedynczy osobnik = permutacja zadań

Przykład:

```matlab
perm = randperm(N); [cite_start]% [cite: 944]

```

**Ocena osobnika** Każdy osobnik oceniany jest za pomocą funkcji celu `makespan(P, perm)`.

```matlab
cost = makespan(P, perm); [cite_start]% [cite: 947]
fitness = 1 / cost; [cite_start]% [cite: 948]

```

**Przykład mutacji typu swap**

```matlab
[cite_start]function child = mutate_swap(child) % [cite: 950]
    n = length(child); [cite_start]% [cite: 951]
    i = randi(n); [cite_start]% [cite: 952]
    j = randi(n); [cite_start]% [cite: 953]
    [cite_start]while j == i % [cite: 954]
        j = randi(n); [cite_start]% [cite: 955]
    end %
    child([i j]) = child([j i]); [cite_start]% [cite: 957]
end %

```

**Przykład selekcji turniejowej**

```matlab
[cite_start]function selected = tournament_selection(population, costs, tournamentSize) % [cite: 960]
    popSize = size(population, 1); [cite_start]% [cite: 961]
    idx = randperm(popSize, tournamentSize); [cite_start]% [cite: 962]
    bestIdx = idx(1); [cite_start]% [cite: 963]
    [cite_start]for k = 2:tournamentSize % [cite: 964]
        [cite_start]if costs(idx(k)) < costs(bestIdx) % [cite: 965]
            bestIdx = idx(k); [cite_start]% [cite: 966]
        end %
    end %
    selected = population(bestIdx, :); [cite_start]% [cite: 969]
end %

```

**Przykład prostego krzyżowania OX (Order Crossover)**

```matlab
[cite_start]function child = ox_crossover(parent1, parent2) % [cite: 972]
    n = length(parent1); [cite_start]% [cite: 973]
    child = zeros(1, n); [cite_start]% [cite: 974]
    a = randi(n); [cite_start]% [cite: 975]
    b = randi(n); [cite_start]% [cite: 976]
    [cite_start]if a > b % [cite: 977]
        temp = a; [cite_start]% [cite: 978]
        a = b; [cite_start]% [cite: 979]
        b = temp; [cite_start]% [cite: 980]
    end %
    child(a:b) = parent1(a:b); [cite_start]% [cite: 982]
    remaining = parent2(~ismember(parent2, child)); [cite_start]% [cite: 983]
    pos = [1:a-1, b+1:n]; [cite_start]% [cite: 984]
    child(pos) = remaining; [cite_start]% [cite: 985]
end %

```

**Schemat iteracji GA**

```matlab
[cite_start]for gen = 1:maxGenerations % [cite: 988]

    [cite_start]for i = 1:popSize % [cite: 989]
        costs(i) = makespan(P, population(i,:)); [cite_start]% [cite: 990]
    end %

    newPopulation = zeros(size(population)); [cite_start]% [cite: 992]

    [cite_start]for i = 1:2:popSize % [cite: 993]

        parent1 = tournament_selection(population, costs, 3); [cite_start]% [cite: 994]
        parent2 = tournament_selection(population, costs, 3); [cite_start]% [cite: 995]

        child1 = ox_crossover(parent1, parent2); [cite_start]% [cite: 996]
        child2 = ox_crossover(parent2, parent1); [cite_start]% [cite: 997]

        [cite_start]if rand < mutationRate % [cite: 998]
            child1 = mutate_swap(child1); [cite_start]% [cite: 999]
        end %

        [cite_start]if rand < mutationRate % [cite: 1001]
            child2 = mutate_swap(child2); [cite_start]% [cite: 1002]
        end %

        newPopulation(i,:) = child1; [cite_start]% [cite: 1004]
        [cite_start]if i+1 <= popSize % [cite: 1005]
            newPopulation(i+1,:) = child2; [cite_start]% [cite: 1006]
        end %
    end %

    population = newPopulation; [cite_start]% [cite: 1009]

end %

```

---

1. Sugestie implementacyjne — Ant Colony Optimization

W algorytmie mrówkowym każda mrówka buduje permutację krok po kroku.

**Inicjalizacja feromonów**

```matlab
tau = ones(N, N); [cite_start]% [cite: 1014]

```

Można przyjąć, że `tau(i,j)` oznacza atrakcyjność umieszczenia zadania `j` po zadaniu `i`.

**Heurystyka** Prosta heurystyka może być oparta na sumarycznym czasie przetwarzania zadania:

```matlab
jobTime = sum(P, 2); [cite_start]% [cite: 1018]
eta = 1 ./ jobTime; [cite_start]% [cite: 1019]

```

**Wybór kolejnego zadania**

```matlab
[cite_start]function nextJob = select_next_job(availableJobs, tauRow, eta, alpha, beta) % [cite: 1021]
    prob = zeros(1, length(availableJobs)); [cite_start]% [cite: 1022]
    [cite_start]for k = 1:length(availableJobs) % [cite: 1023]
        j = availableJobs(k); [cite_start]% [cite: 1024]
        prob(k) = (tauRow(j)^alpha) * (eta(j)^beta); [cite_start]% [cite: 1025]
    end %
    prob = prob / sum(prob); [cite_start]% [cite: 1027]
    r = rand; [cite_start]% [cite: 1028]
    cumProb = cumsum(prob); [cite_start]% [cite: 1029]
    nextJob = availableJobs(find(r <= cumProb, 1, 'first')); [cite_start]% [cite: 1030]
end %

```

**Budowa pojedynczego rozwiązania przez mrówkę**

```matlab
[cite_start]function perm = build_ant_solution(P, tau, eta, alpha, beta) % [cite: 1033]
    N = size(P,1); [cite_start]% [cite: 1034]
    perm = zeros(1,N); [cite_start]% [cite: 1035]
    availableJobs = 1:N; [cite_start]% [cite: 1036]
    firstIdx = randi(N); [cite_start]% [cite: 1037]
    perm(1) = availableJobs(firstIdx); [cite_start]% [cite: 1038]
    availableJobs(firstIdx) = []; [cite_start]% [cite: 1039]
    [cite_start]for pos = 2:N % [cite: 1040]
        prevJob = perm(pos-1); [cite_start]% [cite: 1041]

        nextJob = select_next_job(availableJobs, tau(prevJob,:), eta, alpha, beta); [cite_start]% [cite: 1042]

        perm(pos) = nextJob; [cite_start]% [cite: 1043]
        availableJobs(availableJobs == nextJob) = []; [cite_start]% [cite: 1044]
    end %
end %

```

**Aktualizacja feromonów**

```matlab
tau = (1 - rho) * tau; [cite_start]% [cite: 1048]
[cite_start]for ant = 1:numAnts % [cite: 1049]
    perm = antSolutions(ant,:); [cite_start]% [cite: 1050]
    cost = antCosts(ant); [cite_start]% [cite: 1051]

    [cite_start]for k = 1:N-1 % [cite: 1052]
        i = perm(k); [cite_start]% [cite: 1053]
        j = perm(k+1); [cite_start]% [cite: 1054]
        tau(i,j) = tau(i,j) + 1 / cost; [cite_start]% [cite: 1055]
    end %
end %

```

**Schemat iteracji ACO**

```matlab
[cite_start]for iter = 1:maxIters % [cite: 1059]

    [cite_start]for ant = 1:numAnts % [cite: 1060]
        antSolutions(ant,:) = build_ant_solution(P, tau, eta, alpha, beta); [cite_start]% [cite: 1061]
        antCosts(ant) = makespan(P, antSolutions(ant,:)); [cite_start]% [cite: 1062]
    end %

    tau = (1 - rho) * tau; [cite_start]% [cite: 1064]

    [cite_start]for ant = 1:numAnts % [cite: 1065]
        perm = antSolutions(ant,:); [cite_start]% [cite: 1066]
        cost = antCosts(ant); [cite_start]% [cite: 1067]

        [cite_start]for k = 1:N-1 % [cite: 1068]
            i = perm(k); [cite_start]% [cite: 1069]
            j = perm(k+1); [cite_start]% [cite: 1070]
            tau(i,j) = tau(i,j) + 1 / cost; [cite_start]% [cite: 1071]
        end %
    end %

end %

```

---

1. Sugestie implementacyjne — Bees Algorithm

W algorytmie pszczelim część rozwiązań generowana jest losowo, a część powstaje przez lokalne przeszukiwanie najlepszych obszarów.

**Losowe rozwiązania początkowe**

```matlab
[cite_start]for i = 1:numBees % [cite: 1078]
    population(i,:) = randperm(N); [cite_start]% [cite: 1079]
    costs(i) = makespan(P, population(i,:)); [cite_start]% [cite: 1080]
end %

```

**Lokalna modyfikacja rozwiązania** Najprostszy operator: swap.

```matlab
[cite_start]function newPerm = local_search_swap(perm) % [cite: 1084]
    newPerm = perm; [cite_start]% [cite: 1085]
    n = length(perm); [cite_start]% [cite: 1086]
    i = randi(n); [cite_start]% [cite: 1087]
    j = randi(n); [cite_start]% [cite: 1088]
    [cite_start]while j == i % [cite: 1089]
        j = randi(n); [cite_start]% [cite: 1090]
    end %
    newPerm([i j]) = newPerm([j i]); [cite_start]% [cite: 1092]
end %

```

**Przeszukiwanie sąsiedztwa najlepszego rozwiązania**

```matlab
[cite_start]function bestNeighbor = explore_neighborhood(P, perm, nSearch) % [cite: 1095]
    bestNeighbor = perm; [cite_start]% [cite: 1096]
    bestCost = makespan(P, perm); [cite_start]% [cite: 1097]
    [cite_start]for k = 1:nSearch % [cite: 1098]
        candidate = local_search_swap(perm); [cite_start]% [cite: 1099]
        candidateCost = makespan(P, candidate); [cite_start]% [cite: 1100]

        [cite_start]if candidateCost < bestCost % [cite: 1101]
            bestNeighbor = candidate; [cite_start]% [cite: 1102]
            bestCost = candidateCost; [cite_start]% [cite: 1103]
        end %
    end %
end %

```

**Schemat iteracji Bees Algorithm**

```matlab
[cite_start]for iter = 1:maxIters % [cite: 1108]

    [cite_start]for i = 1:numBees % [cite: 1109]
        costs(i) = makespan(P, population(i,:)); [cite_start]% [cite: 1110]
    end %

    [costs, order] = sort(costs); [cite_start]% [cite: 1112]
    population = population(order,:); [cite_start]% [cite: 1113]

    newPopulation = population; [cite_start]% [cite: 1114]

    [cite_start]for i = 1:numEliteSites % [cite: 1115]
        newPopulation(i,:) = explore_neighborhood(P, population(i,:), eliteSearchSize); [cite_start]% [cite: 1116]
    end %

    [cite_start]for i = numEliteSites+1:numSelectedSites % [cite: 1118]
        newPopulation(i,:) = explore_neighborhood(P, population(i,:), selectedSearchSize); [cite_start]% [cite: 1119]
    end %

    [cite_start]for i = numSelectedSites+1:numBees % [cite: 1121]
        newPopulation(i,:) = randperm(N); [cite_start]% [cite: 1122]
    end %

    population = newPopulation; [cite_start]% [cite: 1124]

end %

```
