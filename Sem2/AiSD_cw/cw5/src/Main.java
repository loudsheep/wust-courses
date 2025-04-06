import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        LinkedList<Integer> list = new LinkedList<>(Arrays.asList(5, 2, 9, 1, 7, 3, 6));

        MergeSorter<Integer> mergeSorter = new MergeSorter<>(Comparator.comparingInt(c -> c));
        QuickSorter<Integer> quickSorter = new QuickSorter<>(Comparator.comparingInt(c -> c));
        CountingSorter countingSorter = new CountingSorter();

//        System.out.println(mergeSorter.sort(list));
//        System.out.println(quickSorter.sort(list));


        System.out.println(Arrays.toString(countingSorter.sort(new int[]{1, 5, 3, 2, 8, 9, 3, 6, 2, 6, 1, 8, 9, 4, 5, 3, 0}, 10)));
    }
}