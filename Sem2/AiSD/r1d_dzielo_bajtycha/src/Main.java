import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
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