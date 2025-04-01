import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> insertionTestList = new ArrayList<>(Arrays.asList(76, 71, 5, 57, 12, 50, 20, 93, 20, 55, 62, 3));

        ListSorter<Integer> insertionSort = new InsertionSorter<>(Comparator.comparingInt(c -> c));
        ListSorter<Integer> selectionSort = new SelectionSorter<>(Comparator.comparingInt(c -> c));
        ListSorter<Integer> bubbleSort = new BubbleSorter<>(Comparator.comparingInt(c -> c));
//        ListSorter<Integer> permutationSort = new PermutationSorter<>(Comparator.comparingInt(c -> c));
        ListSorter<Integer> bogoSort = new BogoSort<>(Comparator.comparingInt(c -> c));


//        System.out.println(insertionSort.sort(insertionTestList));
        System.out.println(bogoSort.sort(insertionTestList));
    }

}