import java.util.Iterator;
import java.util.NoSuchElementException;

public class Array2DIterator<T> implements Iterator<T> {
    private final T[][] array;
    private int row = 0;
    private int col = 0;

    public Array2DIterator(T[][] array) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        if (row >= array.length) return false;

        if (col >= array[row].length && row < array.length - 1) return true;

        return col < array[row].length;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        if (col < array[row].length) return array[row][col++];
        col = 0;
        return array[++row][col++];
    }
}
