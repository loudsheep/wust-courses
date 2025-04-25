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
    for (int &x : a) cin >> x;

    vector<vector<int>> children(n);
    for (int i = 1; i < n; ++i)
    {
        int p;
        cin >> p;
        children[--p].push_back(i);
    }

    // Iterative post-order traversal
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

    const int INF = numeric_limits<int>::min();
    vector<vector<int>> dp_sel(n, vector<int>(k + 1, INF));
    vector<vector<int>> dp_not_sel(n, vector<int>(k + 1, INF));
    vector<int> best_v(k + 1), temp(k + 1);

    for (int u : post_order)
    {
        dp_sel[u][1] = a[u];
        dp_not_sel[u][0] = 0;

        for (int child : children[u])
        {
            for (int s = 0; s <= k; ++s)
                best_v[s] = max(dp_sel[child][s], dp_not_sel[child][s]);

            fill(temp.begin(), temp.end(), INF);
            for (int t = 0; t <= k; ++t)
            {
                if (dp_not_sel[u][t] == INF) continue;
                for (int s = 0; s + t <= k; ++s)
                {
                    if (best_v[s] == INF) continue;
                    temp[t + s] = max(temp[t + s], dp_not_sel[u][t] + best_v[s]);
                }
            }

            copy(temp.begin(), temp.end(), dp_not_sel[u].begin());
        }
    }

    int result = max(dp_sel[0][k], dp_not_sel[0][k]);
    cout << (result < 0 ? -1 : result) << '\n';

    return 0;
}
