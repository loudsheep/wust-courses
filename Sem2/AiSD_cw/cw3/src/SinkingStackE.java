public class SinkingStackE<T> {
    private final T[] stack;
    private int top;
    private final int capacity;
    private int size;

    @SuppressWarnings("unchecked")
    public SinkingStackE(int capacity) {
        this.capacity = capacity;
        this.stack = (T[]) new Object[capacity];
        this.top = 0;
        this.size = 0;
    }

    public void push(T item) {
        stack[top] = item;
        top = (top + 1) % capacity;
        size = Math.min(size + 1, capacity);
    }

    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Empty stack");
        }
        top = (top - 1 + capacity) % capacity;
        size--;
        return stack[top];
    }

    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Empty stack");
        }
        return stack[(top - 1 + capacity) % capacity];
    }

    public boolean isEmpty() {
        return size <= 0;
    }
}
