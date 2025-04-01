import java.util.List;

public interface ListSorter<T> {
    List<T> sort(List<T> list);

    default void printList(List<T> list) {
        System.out.println(list);
    }
}