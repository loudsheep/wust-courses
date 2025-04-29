#include <iostream>
#include <vector>
#include <stack>
#include <algorithm>
#include <numeric>
#include <limits>

using namespace std;

const int K = 501;
const int INF = numeric_limits<int>::min();

int main()
{
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    // Wejście: k, n
    int n, k;
    cin >> n >> k;
    // Wejście: wartości obrazów
    vector<int> a(n);
    for (int &x : a)
        cin >> x;

    // Wejście: wczytanie dzieci
    vector<vector<int>> children(n);
    for (int i = 1; i < n; ++i)
    {
        int p;
        cin >> p;
        --p;
        children[p].push_back(i);
    }

    // Z drzewa tworzymy listę w kolejności post-order, używając stosu
    vector<int> post_order;
    post_order.reserve(n);
    stack<pair<int, bool>> stk;
    stk.emplace(0, false);
    while (!stk.empty())
    {
        auto [node, visited] = stk.top();
        stk.pop();
        if (visited)
        {
            post_order.push_back(node);
        }
        else
        {
            stk.emplace(node, true);
            for (auto it = children[node].rbegin(); it != children[node].rend(); ++it)
                stk.emplace(*it, false);
        }
    }

    // Inicjalizacja małymi wartościami tablic
    static int dp_sel[100000][K];       // dp_sel[u][i] - wierzchołek wybrany 
    static int dp_not_sel[100000][K];   // dp_not_sel[u][i] - wierzchołek nie wybrany

    for (int i = 0; i < n; ++i)
    {
        fill(dp_sel[i], dp_sel[i] + k + 1, INF);
        fill(dp_not_sel[i], dp_not_sel[i] + k + 1, INF);
    }

    // Tymczasowe tablice wyników (używane wiele razy)
    static int best[K];
    static int tmp[K];

    // Dla każdego dziecka w post-order
    for (int u : post_order)
    {
        dp_sel[u][1] = a[u];    // Jeżeli wybierzemy tylko ten node wtedy suma to wartość tego noda, reszty sie nie da wybrać
        dp_not_sel[u][0] = 0;   // Dla 0 wybranych suma to 0

        // Dwie grupy dzieci, A - dla myłych rozwiązań <= 1 node, B - dla większych rozw.
        vector<int> groupA, groupB;

        for (int child : children[u])
        {
            // Dla każdego dziecka sprawdzamy przy jakiej maksymalen ilości wybranych istnieje rozwiązanie
            int max_j_child = 0;
            for (int j = 0; j <= k; ++j)
            {
                // Sprawdzenie czy którekolwiek rozwiązanie istnieje
                best[j] = max(dp_sel[child][j], dp_not_sel[child][j]);
                if (best[j] != INF)
                {
                    max_j_child = j;
                }
            }
            if (max_j_child <= 1)
            {
                groupA.push_back(child);    // małe rozwiązanie do zaoferowania przez dziecko
            }
            else
            {
                groupB.push_back(child);    // większe rozwiązanie
            }
        }

        // Najpierw obliczamy wieksze rozwiązania (pełne DP, sprawdzamy każdą kombinację)
        for (int child : groupB)
        {
            // Tymczasowa tablica z najlepszymi wynikami dla j wybranych
            fill(best, best + k + 1, INF);
            for (int j = 0; j <= k; ++j)
            {
                best[j] = max(dp_sel[child][j], dp_not_sel[child][j]);
            }

            // Znajdujemy każdą kombinację i wpisujemy do tablicy tmp
            fill(tmp, tmp + k + 1, INF);
            for (int i = 0; i <= k; ++i)
            {
                // Wybór nie możliwy, pomijamy
                if (dp_not_sel[u][i] == INF)
                    continue;

                // Wybieramy od 0 do k, ale musimy uwzględnić że już wybraliśmy 'i' kombinacji
                for (int j = 0; j + i <= k; ++j)
                {
                    if (best[j] == INF)
                        continue;
                    // Znajdujemy największą wartość i wpisujemy do tablicy
                    if (dp_not_sel[u][i] + best[j] > tmp[i + j])
                    {
                        tmp[i + j] = dp_not_sel[u][i] + best[j];
                    }
                }
            }

            // Aktualizacja wyników z tmp
            for (int i = 0; i <= k; ++i)
            {
                dp_not_sel[u][i] = tmp[i];
            }
        }

        // Sprawdzamy tylko jeżeli groupA nie jest pusta
        if (!groupA.empty())
        {
            int sum_best0 = 0;  // suma, gdy nic nie zostało wybrane z dzieci
            vector<int> deltas;
            for (int child : groupA)
            {
                int best0 = max(dp_sel[child][0], dp_not_sel[child][0]);                    // 0 wybranych z dziecka
                int best1 = (k >= 1) ? max(dp_sel[child][1], dp_not_sel[child][1]) : INF;   // 1 wybrany z dziecka
                sum_best0 += best0;
                int delta = (best1 != INF) ? (best1 - best0) : INF;     // zysk z wybrania 1 zamiast 0 z dziecka

                if (delta != INF)
                {
                    deltas.push_back(delta);
                }
                else
                {
                    deltas.push_back(-1e9);
                }
            }

            // sortujemy po delcie, malejąco -> największy zysk z wybrania na początek
            sort(deltas.rbegin(), deltas.rend());

            vector<int> prefix_sum;     // Suma prefixowana delt
            int current_sum = 0;
            prefix_sum.push_back(0);
            for (int delta : deltas)
            {
                // Liczymy tylko jeżeli zmiana zwiększa wynik, czyli delta > 0
                if (delta <= 0)
                    break;
                current_sum += delta;
                prefix_sum.push_back(current_sum);
            }

            // Maksymalna ilość dzieci, które można wziąć - albo k, albo ilość delt > 0
            int max_j_a = min((int)prefix_sum.size() - 1, k);

            fill(tmp, tmp + k + 1, INF);
            tmp[0] = sum_best0;
            for (int j_a = 1; j_a <= max_j_a; ++j_a)
            {
                // Dodajemy sumę nie wybranych oraz po kolei coraz więcej wybranych dzieci
                tmp[j_a] = sum_best0 + prefix_sum[j_a];
            }

            fill(best, best + k + 1, INF);
            for (int i = 0; i <= k; ++i)
            {
                // Pomijamy niemożliwe wyniki
                if (dp_not_sel[u][i] == INF)
                    continue;

                for (int j_a = 0; j_a <= max_j_a; ++j_a)
                {
                    if (tmp[j_a] == INF)
                        continue;

                    // Jeżeli ilość wybranych > k to pomijamy
                    int total_j = i + j_a;
                    if (total_j > k)
                        continue;

                    // Wpisujemy do rozwiązania najlepsze wybory
                    if (dp_not_sel[u][i] + tmp[j_a] > best[total_j])
                    {
                        best[total_j] = dp_not_sel[u][i] + tmp[j_a];
                    }
                }
            }

            // Wpisujemy do tablicy wyników tylko lepsze wybory niz do tejpory
            for (int i = 0; i <= k; ++i)
            {
                if (best[i] > dp_not_sel[u][i])
                {
                    dp_not_sel[u][i] = best[i];
                }
            }
        }
    }

    int result = max(dp_sel[0][k], dp_not_sel[0][k]);
    cout << (result < 0 ? -1 : result) << '\n';

    return 0;
}