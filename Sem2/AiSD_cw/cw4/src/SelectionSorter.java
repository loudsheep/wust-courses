import java.util.Comparator;
import java.util.List;

public class SelectionSorter<T> implements ListSorter<T> {
    private final Comparator<T> _comparator;

    public SelectionSorter(Comparator<T> comparator) {
        _comparator = comparator;
    }

    private void printList(List<T> list) {
        System.out.println(list);
    }

    private void swapElements(List<T> list, int leftIdx, int rightIdx) {
        if (leftIdx == rightIdx) return;

        T tmp = list.get(leftIdx);
        list.set(leftIdx, list.get(rightIdx));
        list.set(rightIdx, tmp);
    }

    public List<T> sort(List<T> list) {
        int size = list.size();
        for (int i = size - 1; i > 0; i--) {
            int minPos = i;

            for (int j = i - 1; j >= 0; j--) {
                if (_comparator.compare(list.get(j), list.get(minPos)) <= 0) {
                    minPos = j;
                }
            }

            swapElements(list, minPos, i);
            printList(list);
        }

        return list;
    }

}