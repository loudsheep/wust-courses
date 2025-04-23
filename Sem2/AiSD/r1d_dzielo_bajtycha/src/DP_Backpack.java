import java.util.*;

public class DP_Backpack {
    static int[][] dpSel, dpNotSel;
    static List<Integer>[] children;
    static int[] a;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int k = scanner.nextInt();
        a = new int[n];

        for (int i = 0; i < n; i++) {
            a[i] = scanner.nextInt();
        }

        int[] parent = new int[n - 1];
        for (int i = 0; i < n - 1; i++) {
            parent[i] = scanner.nextInt() - 1; // 0-based index
        }

        // Build tree
        children = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            children[i] = new ArrayList<>();
        }

        for (int i = 0; i < n - 1; i++) {
            children[parent[i]].add(i + 1);
        }

        // Post-order traversal
        List<Integer> postOrder = new ArrayList<>();
        Deque<Pair> stack = new ArrayDeque<>();
        stack.push(new Pair(0, false));
        while (!stack.isEmpty()) {
            Pair p = stack.pop();
            int node = p.node;
            boolean visited = p.visited;
            if (visited) {
                postOrder.add(node);
                continue;
            }
            stack.push(new Pair(node, true));
            List<Integer> ch = children[node];
            for (int i = ch.size() - 1; i >= 0; i--) {
                stack.push(new Pair(ch.get(i), false));
            }
        }

        dpSel = new int[n][k + 1];
        dpNotSel = new int[n][k + 1];
        for (int i = 0; i < n; i++) {
            Arrays.fill(dpSel[i], Integer.MIN_VALUE);
            Arrays.fill(dpNotSel[i], Integer.MIN_VALUE);
        }

        for (int u : postOrder) {
            dpSel[u][1] = a[u];
            for (int m = 2; m <= k; m++) {
                dpSel[u][m] = Integer.MIN_VALUE;
            }

            dpNotSel[u][0] = 0;

            for (int child : children[u]) {
                int[] bestV = new int[k + 1];
                for (int s = 0; s <= k; s++) {
                    bestV[s] = Math.max(dpSel[child][s], dpNotSel[child][s]);
                }

                int[] temp = new int[k + 1];
                Arrays.fill(temp, Integer.MIN_VALUE);
                for (int t = 0; t <= k; t++) {
                    if (dpNotSel[u][t] == Integer.MIN_VALUE) continue;
                    for (int s = 0; s + t <= k; s++) {
                        if (bestV[s] == Integer.MIN_VALUE) continue;
                        temp[t + s] = Math.max(temp[t + s], dpNotSel[u][t] + bestV[s]);
                    }
                }

                for (int m = 0; m <= k; m++) {
                    dpNotSel[u][m] = Math.max(dpNotSel[u][m], temp[m]);
                }
            }

            dpNotSel[u][0] = 0;
        }

        int result = Math.max(dpSel[0][k], dpNotSel[0][k]);
        System.out.println(result < 0 ? -1 : result);
    }

    static class Pair {
        int node;
        boolean visited;

        Pair(int node, boolean visited) {
            this.node = node;
            this.visited = visited;
        }
    }
}
