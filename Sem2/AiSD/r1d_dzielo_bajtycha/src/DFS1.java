import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DFS1 {
    private static int n, k;
    private static int[] values;
    private static List<List<Integer>> tree;
    private static long[][] dp;

    public static void calculate(int n, int k, int[] values, List<List<Integer>> tree) {
        DFS1.n = n;
        DFS1.k = k;
        DFS1.values = values;
        DFS1.tree = tree;
        DFS1.dp = new long[n + 1][k + 1];

        dfs(1);

        long result = dp[1][k];
        if (result == Long.MIN_VALUE) {
            System.out.println(-1);
        } else {
            System.out.println(result);
        }
    }

    private static void dfs(int node) {
        // nic nie wybrane
        dp[node][0] = 0;
        // wybrane tylko ten jeden obraz
        dp[node][1] = values[node];


        for (int child : tree.get(node)) {
            dfs(child);

            long[] tmp = new long[k + 1];

            Arrays.fill(tmp, Long.MIN_VALUE);

            for (int i = 0; i <= k; i++) {
                if (dp[node][i] == Long.MIN_VALUE) continue;

                for (int j = 0; j <= k - i; j++) {
                    if (dp[child][j] == Long.MIN_VALUE) continue;
                    tmp[i + j] = Math.max(tmp[i + j], dp[node][i] + dp[child][j]);
                }
            }

            for (int i = 0; i <= k; i++) {
                dp[node][i] = tmp[i];
            }
        }

        // jak się nie da osiągnąć rozmiaru zbiory to ustawiamy brak możliwości
        for (int i = 0; i <= k; i++) {
            if (dp[node][i] == 0 && i != 0 && i != 1) {
                dp[node][i] = Long.MIN_VALUE;
            }
        }
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        int n = scan.nextInt();
        int k = scan.nextInt();

        int[] values = new int[n + 1]; // index od 1 dla wygody
        for (int i = 1; i <= n; i++) {
            values[i] = scan.nextInt();
        }

        List<List<Integer>> tree = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            tree.add(new ArrayList<>());
        }

        for (int i = 2; i <= n; i++) {
            int parent = scan.nextInt();
            tree.get(parent).add(i); // parent -> i
        }
        scan.close();

        DFS1.calculate(n, k, values, tree);
    }
}
