import java.util.Comparator;
import java.util.List;

public class HeapSorter<T> implements ListSorter<T> {
    private final Comparator<T> _comparator;

    public HeapSorter(Comparator<T> comparator) {
        _comparator = comparator;
    }

    private void swapElements(List<T> list, int idx1, int idx2) {
        if (idx1 == idx2) return;

        T tmp = list.get(idx1);
        list.set(idx1, list.get(idx2));
        list.set(idx2, tmp);
    }

    private void heap(List<T> list, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && _comparator.compare(list.get(left), list.get(largest)) > 0) {
            largest = left;
        }

        if (right < n &&  _comparator.compare(list.get(right), list.get(largest)) > 0) {
            largest = right;
        }

        if (largest != i) {
            swapElements(list, i, largest);

            heap(list, n, largest);
        }
    }


    public List<T> sort(List<T> list) {
        int n = list.size();

        for (int i = n / 2 - 1; i >= 0; i--) {
            heap(list, n, i);
        }

        for (int i = n - 1; i > 0; i--) {
            swapElements(list, 0, i);
            heap(list, i, 0);
        }

        return list;
    }
}
