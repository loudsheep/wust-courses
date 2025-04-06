import java.util.Comparator;
import java.util.List;

public class ShakerSorter<T> implements ListSorter<T> {
    private final Comparator<T> _comparator;

    public ShakerSorter(Comparator<T> comparator) {
        _comparator = comparator;
    }

    private void swapElements(List<T> list, int idx1, int idx2) {
        if (idx1 == idx2) return;
        T tmp = list.get(idx1);
        list.set(idx1, list.get(idx2));
        list.set(idx2, tmp);
    }

    // ulepszenia:
    // - sprawdzanie czy posortowany
    // - zapamiętywanie ostatnie zmiany
    public List<T> sort(List<T> list) {
        int left = 0;
        int right = list.size() - 1;
        boolean swapped;

        do {
            swapped = false;

            // forward
            int lastSwapPos = right - 1;
            for (int i = left; i < right; i++) {
                if (_comparator.compare(list.get(i), list.get(i + 1)) > 0) {
                    swapElements(list, i, i + 1);
                    swapped = true;
                    lastSwapPos = i;
                }
            }
            right = lastSwapPos;

            printList(list);

            if (!swapped) break;
            swapped = false;

            // backward
            lastSwapPos = left + 1;
            for (int i = right; i > left; i--) {
                if (_comparator.compare(list.get(i - 1), list.get(i)) > 0) {
                    swapElements(list, i - 1, i);
                    swapped = true;
                    lastSwapPos = i;
                }
            }
            left = lastSwapPos;

            printList(list);

        } while (swapped);

        return list;
    }
}
