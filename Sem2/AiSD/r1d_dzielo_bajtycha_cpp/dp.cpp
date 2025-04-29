#include <iostream>
#include <vector>
#include <algorithm>
#include <stack>
#include <limits>

using namespace std;

int main()
{
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, k;
    cin >> n >> k;
    vector<int> a(n);
    for (int &x : a)
        cin >> x;

    vector<int> parent(n - 1);
    for (int i = 0; i < n - 1; ++i)
    {
        cin >> parent[i];
        parent[i]--; // 0-based indexing
    }

    // Build the tree
    vector<vector<int>> children(n);
    for (int i = 0; i < n - 1; ++i)
    {
        int p = parent[i];
        children[p].push_back(i + 1); // node i+1 has parent p
    }

    // Post-order traversal
    vector<int> post_order;
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
            {
                stk.emplace(*it, false);
            }
        }
    }

    const int INF = numeric_limits<int>::min();
    vector<vector<int>> dp_sel(n, vector<int>(k + 1, INF));
    vector<vector<int>> dp_not_sel(n, vector<int>(k + 1, INF));

    for (int u : post_order)
    {
        dp_sel[u][1] = a[u];
        for (int m = 2; m <= k; ++m)
            dp_sel[u][m] = INF;

        dp_not_sel[u][0] = 0;
        for (int child : children[u])
        {
            vector<int> best_v(k + 1, INF);
            for (int s = 0; s <= k; ++s)
            {
                best_v[s] = max(dp_sel[child][s], dp_not_sel[child][s]);
            }

            vector<int> temp(k + 1, INF);
            for (int t = 0; t <= k; ++t)
            {
                if (dp_not_sel[u][t] == INF)
                    continue;
                for (int s = 0; s + t <= k; ++s)
                {
                    if (best_v[s] == INF)
                        continue;
                    temp[t + s] = max(temp[t + s], dp_not_sel[u][t] + best_v[s]);
                }
            }

            for (int m = 0; m <= k; ++m)
            {
                dp_not_sel[u][m] = max(dp_not_sel[u][m], temp[m]);
            }
        }

        dp_not_sel[u][0] = 0;
    }

    int max_sum = max(dp_sel[0][k], dp_not_sel[0][k]);
    cout << (max_sum < 0 ? -1 : max_sum) << '\n';

    return 0;
}
