import java.util.Iterator;

public class NextNumberGenerator implements Iterator<Integer> {
    private int current;

    public NextNumberGenerator(int start) {
        this.current = start;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Integer next() {
        return current++;
    }
}
