import java.util.Comparator;
import java.util.List;

public class BubbleSorter<T> implements ListSorter<T> {
    private final Comparator<T> _comparator;

    public BubbleSorter(Comparator<T> comparator) {
        _comparator = comparator;
    }

    private void swapElements(List<T> list, int idx1, int idx2) {
        if (idx1 == idx2) return;

        T tmp = list.get(idx1);
        list.set(idx1, list.get(idx2));
        list.set(idx2, tmp);
    }

    public List<T> sort(List<T> list) {
        int size = list.size();

        for (int i = 1; i < size; i++) {
            for (int j = 0; j < size - i; j++) {
                int r = j + 1;
                printList(list);
                if (_comparator.compare(list.get(j), list.get(r)) <= 0) {
                    swapElements(list, j, r);
                }
            }
        }

        return list;
    }
}
