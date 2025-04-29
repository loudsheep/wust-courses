#include <iostream>
#include <vector>
#include <stack>
#include <algorithm>
#include <array>
#include <limits>

using namespace std;

constexpr int K = 501;
constexpr int INF = numeric_limits<int>::min();

struct DP {
    array<int, K> sel{};
    array<int, K> not_sel{};

    DP() {
        sel.fill(INF);
        not_sel.fill(INF);
    }
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, k;
    cin >> n >> k;
    vector<int> a(n);
    for (int& x : a) cin >> x;

    vector<vector<int>> children(n);
    for (int i = 1; i < n; ++i) {
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
    while (!stk.empty()) {
        auto [u, visited] = stk.top();
        stk.pop();
        if (visited) {
            post_order.push_back(u);
        } else {
            stk.emplace(u, true);
            for (auto it = children[u].rbegin(); it != children[u].rend(); ++it)
                stk.emplace(*it, false);
        }
    }

    vector<DP> dp(n);
    array<int, K> best, temp;

    for (int u : post_order) {
        dp[u].sel[1] = a[u];
        dp[u].not_sel[0] = 0;

        for (int v : children[u]) {
            // best[i] = max(dp[v].sel[i], dp[v].not_sel[i])
            for (int i = 0; i <= k; ++i)
                best[i] = max(dp[v].sel[i], dp[v].not_sel[i]);

            // temp = copy of dp[u].not_sel
            temp = dp[u].not_sel;

            for (int i = 0; i <= k; ++i) {
                if (dp[u].not_sel[i] == INF) continue;
                for (int j = 0; j <= k - i; ++j) {
                    if (best[j] != INF) {
                        temp[i + j] = max(temp[i + j], dp[u].not_sel[i] + best[j]);
                    }
                }
            }

            dp[u].not_sel = temp;
        }
    }

    int result = max(dp[0].sel[k], dp[0].not_sel[k]);
    cout << (result < 0 ? -1 : result) << '\n';
}
