import java.util.Comparator;
import java.util.List;

public class BogoSort<T> implements ListSorter<T>{
    private final Comparator<T> _comparator;

    public BogoSort(Comparator<T> comparator) {
        _comparator = comparator;
    }

    private void swapElements(List<T> list, int idx1, int idx2) {
        if (idx1 == idx2) return;

        T tmp = list.get(idx1);
        list.set(idx1, list.get(idx2));
        list.set(idx2, tmp);
    }

    private boolean isSorted(List<T> list) {
        for (int i = 1; i < list.size(); i++) {
            if (_comparator.compare(list.get(i - 1), list.get(i)) > 0) {
                return false;
            }
        }
        return true;
    }

    public List<T> sort(List<T> list) {
        int size = list.size();
        int c = 0;
        while(!isSorted(list)) {
            for (int i = 0; i < size; i++) {
                int swapIdx = (int) (Math.random() * size);
                swapElements(list, swapIdx, i);
            }
            c++;
        }

        System.out.println("Count: " + c);
        return list;
    }
}
