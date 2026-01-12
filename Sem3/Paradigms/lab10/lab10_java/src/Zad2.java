import java.util.Arrays;

public class Zad2 {
    public static int[] duplicate(int[] arr, int[] counts) {
        int totalSize = 0;
        int limit = Math.min(arr.length, counts.length);

        for (int i = 0; i < limit; i++) {
            if (counts[i] < 0) throw new IllegalArgumentException("Negative count value");
            totalSize += counts[i];
        }

        int[] result = new int[totalSize];
        int k = 0;
        for (int i = 0; i < limit; i++) {
            for (int j = 0; j < counts[i]; j++) {
                result[k++] = arr[i];
            }
        }

        return result;
    }


    public static void main(String[] args) {
        boolean res1 = Arrays.equals(duplicate(new int[]{1, 2, 3}, new int[]{0, 3, 1, 4}), new int[]{2, 2, 2, 3});
        boolean res2 = Arrays.equals(duplicate(new int[]{10, 20}, new int[]{2, 1}), new int[]{10, 10, 20});
        boolean res3 = Arrays.equals(duplicate(new int[]{5, 6, 7}, new int[]{}), new int[]{});
        boolean res4 = Arrays.equals(duplicate(new int[]{1, 2, 3}, new int[]{2, 1}), new int[]{1, 1, 2});
        boolean res5 = Arrays.equals(duplicate(new int[]{8}, new int[]{3, 5, 2}), new int[]{8, 8, 8});
        boolean res6 = Arrays.equals(duplicate(new int[]{}, new int[]{3, 5, 2}), new int[]{});

        System.out.println(res1);
        System.out.println(res2);
        System.out.println(res3);
        System.out.println(res4);
        System.out.println(res5);
        System.out.println(res6);
    }
}
