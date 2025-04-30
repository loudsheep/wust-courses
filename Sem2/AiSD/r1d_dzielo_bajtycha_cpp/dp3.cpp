#include <iostream>
#include <vector>
#include <stack>
#include <algorithm>
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

    // Use fixed-size arrays for speed
    static int dp_sel[100000][K];
    static int dp_not_sel[100000][K];

    for (int i = 0; i < n; ++i)
        for (int j = 0; j <= k; ++j)
            dp_sel[i][j] = dp_not_sel[i][j] = INF;

    static int temp[K], best[K];

    for (int u : post_order)
    {
        dp_sel[u][1] = a[u];
        dp_not_sel[u][0] = 0;

        for (int child : children[u])
        {
            for (int i = 0; i <= k; ++i)
                best[i] = max(dp_sel[child][i], dp_not_sel[child][i]);

            for (int i = 0; i <= k; ++i)
                temp[i] = INF;

            for (int i = 0; i <= k; ++i)
            {
                if (dp_not_sel[u][i] == INF)
                    continue;
                for (int j = 0; j + i <= k; ++j)
                {
                    if (best[j] == INF)
                        continue;
                    temp[i + j] = max(temp[i + j], dp_not_sel[u][i] + best[j]);
                }
            }

            for (int i = 0; i <= k; ++i)
                dp_not_sel[u][i] = temp[i];
        }
    }

    int result = max(dp_sel[0][k], dp_not_sel[0][k]);
    cout << (result < 0 ? -1 : result) << '\n';

    return 0;
}
