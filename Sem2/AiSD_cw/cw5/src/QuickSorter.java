import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class QuickSorter<T> implements ListSorter<T> {
    private final Comparator<T> _comparator;
    private final Random rand;

    public QuickSorter(Comparator<T> comparator) {
        _comparator = comparator;
        rand = new Random();
    }

    private void swapElements(List<T> list, int idx1, int idx2) {
        if (idx1 == idx2) return;

        T tmp = list.get(idx1);
        list.set(idx1, list.get(idx2));
        list.set(idx2, tmp);
    }

    private int selectPivotIndex(List<T> list, int left, int right, Random rand) {
        int size = right - left + 1;

        if (size > 100) {
            int i1 = left + rand.nextInt(size);
            int i2 = left + rand.nextInt(size);
            int i3 = left + rand.nextInt(size);

            List<Integer> indices = Arrays.asList(i1, i2, i3);

            indices.sort((a, b) -> _comparator.compare(list.get(a), list.get(b)));
//            System.out.println("three " + indices.get(1));
            return indices.get(1);
        } else {
//            System.out.println("last " + right);
            return right;
        }
    }
    private int partition(List<T> list, int nFrom, int nTo) {
        int pivotIdx = selectPivotIndex(list, nFrom, nTo, rand);
        T picot = list.get(pivotIdx);
        int i = nFrom - 1;

        for (int j = nFrom; j < nTo; j++) {
            if (_comparator.compare(list.get(j), picot) <= 0) {
                i++;

                swapElements(list, i, j);
            }
        }

        i++;
        swapElements(list, i, pivotIdx);

        return i;
    }

    //
    private void quickSort(List<T> list, int startIndex, int endIndex) {
        if (startIndex < endIndex) {
            int partition = partition(list, startIndex, endIndex);

            quickSort(list, startIndex, partition - 1);
            quickSort(list, partition + 1, endIndex);
        }
    }

    public List<T> sort(List<T> list) {
        quickSort(list, 0, list.size() - 1);
        return list;
    }
}
