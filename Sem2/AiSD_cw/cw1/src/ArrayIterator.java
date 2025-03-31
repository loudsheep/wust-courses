import java.util.*;

public class ArrayIterator<T> implements Iterator<T> {
    private T[] array;
    private int pos = 0;

    public ArrayIterator(T[] anArray) {
        array = anArray;
    }

    public boolean hasNext() {
        return pos < array.length;
    }

    public T next() throws NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        return array[pos++];
    }

    public T[] removeElements(T[] input, T elem) {
        List<T> result = Arrays.asList(input);

        for (T item : input) {
            if (!elem.equals(item)) {
                result.add(item);
            }
        }

        return result.toArray(input);
    }

    public void remove() {
        this.array = removeElements(array, array[pos]);
    }
}