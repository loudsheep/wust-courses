import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        LinkedList<Integer> list = new LinkedList<>(Arrays.asList(5, 2, 9, 1, 7, 3, 6));
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            list.add(r.nextInt(1000));
        }

        MergeSorter<Integer> mergeSorter = new MergeSorter<>(Comparator.comparingInt(c -> c));
        QuickSorter<Integer> quickSorter = new QuickSorter<>(Comparator.comparingInt(c -> c));
        HeapSorter<Integer> heapSorter = new HeapSorter<>(Comparator.comparingInt(c -> c));
        CountingSorter countingSorter = new CountingSorter();

//        System.out.println(mergeSorter.sort(list));
//        System.out.println(quickSorter.sort(list));
//        System.out.println(heapSorter.sort(list));


        System.out.println(Arrays.toString(countingSorter.sort(new int[]{0, 2, 1, 0, 4, 4, 2, 1, 1, 1}, 4)));
    }
}