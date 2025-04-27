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

    int n, k;
    cin >> n >> k;
    vector<int> a(n);
    for (int &x : a)
        cin >> x;

    vector<vector<int>> children(n);
    for (int i = 1; i < n; ++i)
    {
        int p;
        cin >> p;
        --p;
        children[p].push_back(i);
    }

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

    static int dp_sel[100000][K];
    static int dp_not_sel[100000][K];

    for (int i = 0; i < n; ++i)
    {
        fill(dp_sel[i], dp_sel[i] + k + 1, INF);
        fill(dp_not_sel[i], dp_not_sel[i] + k + 1, INF);
    }

    static int best[K];
    static int temp[K];

    for (int u : post_order)
    {
        dp_sel[u][1] = a[u];
        dp_not_sel[u][0] = 0;

        vector<int> groupA, groupB;

        for (int child : children[u])
        {
            int max_j_child = 0;
            for (int j = 0; j <= k; ++j)
            {
                best[j] = max(dp_sel[child][j], dp_not_sel[child][j]);
                if (best[j] != INF)
                {
                    max_j_child = j;
                }
            }
            if (max_j_child <= 1)
            {
                groupA.push_back(child);
            }
            else
            {
                groupB.push_back(child);
            }
        }

        for (int child : groupB)
        {
            fill(best, best + k + 1, INF);
            for (int j = 0; j <= k; ++j)
            {
                best[j] = max(dp_sel[child][j], dp_not_sel[child][j]);
            }

            fill(temp, temp + k + 1, INF);
            for (int i = 0; i <= k; ++i)
            {
                if (dp_not_sel[u][i] == INF)
                    continue;
                for (int j = 0; j + i <= k; ++j)
                {
                    if (best[j] == INF)
                        continue;
                    if (dp_not_sel[u][i] + best[j] > temp[i + j])
                    {
                        temp[i + j] = dp_not_sel[u][i] + best[j];
                    }
                }
            }
            for (int i = 0; i <= k; ++i)
            {
                dp_not_sel[u][i] = temp[i];
            }
        }

        if (!groupA.empty())
        {
            int sum_best0 = 0;
            vector<int> deltas;
            for (int child : groupA)
            {
                int best0 = max(dp_sel[child][0], dp_not_sel[child][0]);
                int best1 = (k >= 1) ? max(dp_sel[child][1], dp_not_sel[child][1]) : INF;
                sum_best0 += best0;
                int delta = (best1 != INF) ? (best1 - best0) : INF;
                if (delta != INF)
                {
                    deltas.push_back(delta);
                }
                else
                {
                    deltas.push_back(-1e9);
                }
            }

            sort(deltas.rbegin(), deltas.rend());

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

            int max_j_a = min((int)prefix_sum.size() - 1, k);

            fill(temp, temp + k + 1, INF);
            temp[0] = sum_best0;
            for (int j_a = 1; j_a <= max_j_a; ++j_a)
            {
                temp[j_a] = sum_best0 + prefix_sum[j_a];
            }

            fill(best, best + k + 1, INF);
            for (int i = 0; i <= k; ++i)
            {
                if (dp_not_sel[u][i] == INF)
                    continue;
                for (int j_a = 0; j_a <= max_j_a; ++j_a)
                {
                    if (temp[j_a] == INF)
                        continue;
                    int total_j = i + j_a;
                    if (total_j > k)
                        continue;
                    if (dp_not_sel[u][i] + temp[j_a] > best[total_j])
                    {
                        best[total_j] = dp_not_sel[u][i] + temp[j_a];
                    }
                }
            }

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