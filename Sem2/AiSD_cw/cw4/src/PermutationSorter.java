import java.util.Comparator;
import java.util.List;

public class PermutationSorter<T> implements ListSorter<T> {
    private final Comparator<T> _comparator;

    public PermutationSorter(Comparator<T> comparator) {
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

    private List<T> permute(List<T> list, int index) {
        if (index == list.size() - 1) {
            if (isSorted(list)) return list;
            return null;
        }

        for (int i = index; i < list.size(); i++) {
            swapElements(list, index, i);
            List<T> result = permute(list, index + 1);
            if (result != null) return result;
            swapElements(list, index, i); // backtrack
        }

        return null;
    }

    public List<T> sort(List<T> list) {
        List<T> result = permute(list, 0);

        if (result != null) return result;

        return list;
    }
}
