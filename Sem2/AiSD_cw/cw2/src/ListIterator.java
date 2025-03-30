import java.util.Iterator;

public interface ListIterator<E> extends Iterator<E> {
    void add(E e); // dodanie e w bieżącej pozycji, ZA kursor

    boolean hasNext();

    boolean hasPrevious();// jak hasNext, ale w przeciwnym kierunku

    E next();

    int nextIndex(); // indeks elementu, który byłby zwrócony przez next()

    E previous();

    int previousIndex();

    void remove(); // usuwa ostatnio zwrócony element przez next() lub previous()

    void set(E e);
}