import java.util.Arrays;

public class Zad1 {
    public static int[] insert(int[] arr, int x) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) throw new IllegalArgumentException("Array must be sorted");
        }

        int[] result = new int[arr.length + 1];
        int i = 0;
        int j = 0;
        boolean inserted = false;

        while (i < arr.length) {
            if (!inserted && x <= arr[i]) {
                result[j++] = x;
                inserted = true;
            } else {
                result[j++] = arr[i++];
            }
        }

        if (!inserted) result[j] = x;

        return result;
    }

    public static void main(String[] args) {
        boolean res1 = Arrays.equals(insert(new int[]{1, 3, 5, 7}, 4), new int[]{1, 3, 4, 5, 7});
        boolean res2 = Arrays.equals(insert(new int[]{}, 10), new int[]{10});
        boolean res3 = Arrays.equals(insert(new int[]{1, 2}, 3), new int[]{1, 2, 3});

        System.out.println(res1);
        System.out.println(res2);
        System.out.println(res3);
    }
}
