import java.util.Iterator;
import java.util.NoSuchElementException;

public class PrimeGenerator implements Iterator<Integer> {
    private int n;
    private int current = 2;

    public PrimeGenerator(int n) {
        this.n = n;
    }

    private boolean isPrime(int num) {
        if (num < 2) return false;
        for (int i = 2; i * i <= num; i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    private void findNextPrime() {
        for (int i = current + 1; i < n; i++) {
            if (isPrime(i)) {
                current = i;
                return;
            }
        }
        current = n;
    }

    @Override
    public boolean hasNext() {
        return current < n;
    }

    @Override
    public Integer next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        int value = current;
        findNextPrime();

        return value;
    }
}
