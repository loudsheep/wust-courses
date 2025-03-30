public class SinkingStackIE<T> {
    private T[] stack;
    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public SinkingStackIE(int capacity) {
        this.capacity = capacity;
        this.stack = (T[]) new Object[capacity];
        this.size = 0;
    }

    public void push(T item) {
        if (size == capacity) {
            // ineffective array copy O(n)
            System.arraycopy(stack, 1, stack, 0, capacity - 1);
            stack[capacity - 1] = item;
        } else {
            stack[size++] = item;
        }
    }

    public T pop() {
        if (size == 0) {
            throw new IllegalStateException("Stos jest pusty");
        }
        return stack[--size];
    }

    public T peek() {
        if (size == 0) {
            throw new IllegalStateException("Stos jest pusty");
        }
        return stack[size - 1];
    }

    public boolean isEmpty() {
        return size == 0;
    }
}