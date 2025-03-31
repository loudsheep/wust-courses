import java.util.Iterator;
import java.util.NoSuchElementException;

public class Array2DReverseIterator<T> implements Iterator<T> {
    private final T[][] array;
    private int row ;
    private int col ;

    public Array2DReverseIterator(T[][] array) {
        this.array = array;
        this.row = array.length - 1;
        this.col = row >= 0 ? array[row].length - 1 : -1;
    }

    @Override
    public boolean hasNext() {
        if (row < 0) return false;

        if (col < 0 && row > 0) return true;

        return col >= 0;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        if (col >= 0) return array[row][col--];
        col = array[row - 1].length - 1;
        return array[--row][col--];
    }
}
