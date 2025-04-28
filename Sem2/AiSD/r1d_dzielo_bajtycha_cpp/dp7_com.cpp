#include <iostream>
#include <vector>
#include <stack>
#include <algorithm>
#include <numeric>
#include <limits>

using namespace std;

// Maksymalne K to 500
const int K = 501;
const int INF = numeric_limits<int>::min(); // Definiujemy bardzo małą wartość jako "-inf"

int main()
{
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    // Wejście programu
    int n, k;
    cin >> n >> k;    // Wczytanie liczby wierzchołków i parametru k
    vector<int> a(n); // a[i] - wartość przypisana do wierzchołka i
    for (int &x : a)
        cin >> x;

    vector<vector<int>> children(n); // children[i] - lista dzieci wierzchołka i
    for (int i = 1; i < n; ++i)
    {
        int p;
        cin >> p;                 // Wczytanie ojca wierzchołka i
        --p;                      // Indeksy od 0
        children[p].push_back(i); // Dodajemy i jako dziecko p
    }

    // Tworzenie post-order traversal drzewa (od liści do korzenia) o rozmiarze n
    vector<int> post_order;
    post_order.reserve(n);
    stack<pair<int, bool>> stk; // Stos przechowujący (wierzchołek, czy odwiedzony)
    stk.emplace(0, false);      // Startujemy od korzenia

    // Stwórz post_order kolejność z stosu
    while (!stk.empty())
    {
        auto [node, visited] = stk.top();
        stk.pop();  // Usunięcie najwyższego elementu bo jego odczycie
        if (visited)
        {
            post_order.push_back(node); // Jeśli odwiedzony - dodajemy do kolejności post-order
        }
        else
        {
            stk.emplace(node, true); // Najpierw oznacz jako odwiedzony
            for (auto it = children[node].rbegin(); it != children[node].rend(); ++it)
                stk.emplace(*it, false); // Dodaj dzieci w odwrotnej kolejności
        }
    }

    // Tablice dynamicznego programowania:
    static int dp_sel[100000][K];     // dp_sel[u][j] - maksymalna suma, gdy wierzchołek u jest WYBRANY, a liczba wybranych = j
    static int dp_not_sel[100000][K]; // dp_not_sel[u][j] - jak wyżej, gdy u NIE jest wybrane

    // Inicjalizacja tablic wartościami "-inf"
    for (int i = 0; i < n; ++i)
    {
        fill(dp_sel[i], dp_sel[i] + k + 1, INF);
        fill(dp_not_sel[i], dp_not_sel[i] + k + 1, INF);
    }

    // Bufory pomocnicze do obliczeń
    static int best[K];
    static int tmp[K];

    // Przechodzimy przez wierzchołki w kolejności post-order
    for (int u : post_order)
    {
        dp_sel[u][1] = a[u];  // Jeśli wybieramy u, to musi być 1 wybrany i suma = a[u]
        dp_not_sel[u][0] = 0; // Jeśli nie wybieramy u, to suma = 0 i liczba wybranych = 0

        vector<int> groupA, groupB; // Dzieci dzielimy na grupy po rozmiarze drzewa jakie tworzą

        // Grupowanie dzieci
        for (int child : children[u])
        {
            int max_j_child = 0;
            for (int j = 0; j <= k; ++j)
            {
                best[j] = max(dp_sel[child][j], dp_not_sel[child][j]); // Maksymalne wartości dla dziecka, jeżeli jest wybrane lub nie
                if (best[j] != INF)
                {
                    max_j_child = j; // Maksymalne j dla którego istnieje rozwiązanie
                }
            }
            if (max_j_child <= 1)
            {
                groupA.push_back(child); // Małe poddrzewa (łatwiejsze do scalania)
            }
            else
            {
                groupB.push_back(child); // Większe poddrzewa
            }
        }

        // Obsługa dużych drzew dzieci (groupB)
        for (int child : groupB)
        {
            // Wybierz najlepszą kompinację (wybrany albo nie)
            fill(best, best + k + 1, INF);
            for (int j = 0; j <= k; ++j)
            {
                best[j] = max(dp_sel[child][j], dp_not_sel[child][j]);
            }

            fill(tmp, tmp + k + 1, INF);
            for (int i = 0; i <= k; ++i)
            {
                if (dp_not_sel[u][i] == INF)
                    continue;
                for (int j = 0; j + i <= k; ++j)
                {
                    if (best[j] == INF)
                        continue;
                    if (dp_not_sel[u][i] + best[j] > tmp[i + j])
                    {
                        tmp[i + j] = dp_not_sel[u][i] + best[j]; // Scalamy rozwiązania
                    }
                }
            }
            for (int i = 0; i <= k; ++i)
            {
                dp_not_sel[u][i] = tmp[i]; // Aktualizacja wartości
            }
        }

        // Obsługa małych drezw dzieci (groupA)
        if (!groupA.empty())
        {
            int sum_best0 = 0;  // Suma najlepszych wyników dla 0 wyborów
            vector<int> deltas; // Zmiany przy wyborze dodatkowych dzieci

            // Liczymy podstawowe wartości i różnice
            for (int child : groupA)
            {
                int best0 = max(dp_sel[child][0], dp_not_sel[child][0]);
                int best1 = (k >= 1) ? max(dp_sel[child][1], dp_not_sel[child][1]) : INF;
                sum_best0 += best0;
                int delta = (best1 != INF) ? (best1 - best0) : INF;
                if (delta != INF)
                {
                    deltas.push_back(delta); // Delta jeśli warto wybierać
                }
                else
                {
                    deltas.push_back(-1e9); // Jeśli nie warto wybierać
                }
            }

            sort(deltas.rbegin(), deltas.rend()); // Największe delty pierwsze

            // Prefiksy sum delt
            vector<int> prefix_sum;
            int current_sum = 0;
            prefix_sum.push_back(0);
            for (int delta : deltas)
            {
                if (delta <= 0)
                    break;
                current_sum += delta;
                prefix_sum.push_back(current_sum);
            }

            int max_j_a = min((int)prefix_sum.size() - 1, k); // Ile delt możemy użyć

            fill(tmp, tmp + k + 1, INF);
            tmp[0] = sum_best0;
            for (int j_a = 1; j_a <= max_j_a; ++j_a)
            {
                tmp[j_a] = sum_best0 + prefix_sum[j_a]; // Wyniki dla różnych liczby wybranych dzieci
            }

            // Teraz scalanie ich z obecnym dp_not_sel[u][i]
            fill(best, best + k + 1, INF);
            for (int i = 0; i <= k; ++i)
            {
                if (dp_not_sel[u][i] == INF)
                    continue;
                for (int j_a = 0; j_a <= max_j_a; ++j_a)
                {
                    if (tmp[j_a] == INF)
                        continue;
                    int total_j = i + j_a;
                    if (total_j > k)
                        continue;
                    if (dp_not_sel[u][i] + tmp[j_a] > best[total_j])
                    {
                        best[total_j] = dp_not_sel[u][i] + tmp[j_a];
                    }
                }
            }

            // Aktualizacja
            for (int i = 0; i <= k; ++i)
            {
                if (best[i] > dp_not_sel[u][i])
                {
                    dp_not_sel[u][i] = best[i];
                }
            }
        }
    }

    // Wynik - maksimum spośród wybrania lub nie wybrania korzenia przy dokładnie k wierzchołkach
    int result = max(dp_sel[0][k], dp_not_sel[0][k]);
    cout << (result < 0 ? -1 : result) << '\n'; // Jeśli wynik ujemny - zwracamy -1

    return 0;
}
