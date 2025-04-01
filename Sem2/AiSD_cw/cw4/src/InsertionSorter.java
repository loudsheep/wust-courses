import java.util.Comparator;
import java.util.List;

public class InsertionSorter<T> implements  ListSorter<T>{
    private final Comparator<T> _comparator;

    public InsertionSorter(Comparator<T> comparator) {
        _comparator = comparator;
    }

    public List<T> sort(List<T> list) {
        printList(list);
        for (int i = list.size() - 2; i >= 0; i--) {
            T value = list.get(i);
            T tmp;
            int j = i;
            for (; j < list.size() - 1; j++) {
                if (_comparator.compare(value, tmp = list.get(j + 1)) >= 0) break;

                list.set(j, tmp);
                printList(list);
            }
            list.set(j, value);
            printList(list);
        }

        return list;

    }
}
