import java.util.Iterator;
import java.util.NoSuchElementException;

public class ShuffleIterator<T> implements Iterator<T> {
    private Iterator<T> iter1, iter2;
    private boolean first = true;

    public ShuffleIterator(Iterator<T> iter1, Iterator<T> iter2) {
        this.iter1 = iter1;
        this.iter2 = iter2;
    }

    @Override
    public boolean hasNext() {
        return iter1.hasNext() || iter2.hasNext();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        if (first && iter1.hasNext() || !iter2.hasNext()) {
            first = false;
            return iter1.next();
        }

        first = !first;
        return iter2.next();
    }
}
