import java.util.Iterator;
import java.util.NoSuchElementException;

public class KthIterator<T> implements Iterator<T> {
    private final Iterator<T> iter;
    private final int k;

    public KthIterator(Iterator<T> iter, int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("k must be greater than 0");
        }
        this.iter = iter;
        this.k = k;
    }

    @Override
    public boolean hasNext() {
        return iter.hasNext();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        T value = iter.next();

        for (int i = 0; i < k - 1 && iter.hasNext(); i++) {
            iter.next();
        }

        return value;
    }
}
