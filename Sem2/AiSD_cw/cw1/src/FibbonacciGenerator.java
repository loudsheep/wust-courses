import java.util.Iterator;

public class FibbonacciGenerator implements Iterator<Integer> {
    private int f0, f1;

    public FibbonacciGenerator() {
        this.f0 = 1;
        this.f1 = 1;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Integer next() {
        int f2 = f1 + f0;
        f0 = f1;
        f1 = f2;

        return f2;
    }
}
