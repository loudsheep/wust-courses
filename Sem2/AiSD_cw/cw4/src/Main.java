import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> insertionTestList = new ArrayList<>(Arrays.asList(3, 7, 1, 8, 2));

        ListSorter<Integer> insertionSort = new InsertionSorter<>(Comparator.comparingInt(c -> c));
        ListSorter<Integer> selectionSort = new SelectionSorter<>(Comparator.comparingInt(c -> c));


//        System.out.println(insertionSort.sort(insertionTestList));
        System.out.println(selectionSort.sort(insertionTestList));
    }

}