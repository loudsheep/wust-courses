import java.util.Stack;

public class VelosoTraversableStack<T> extends Stack<T> {
    private int cursor;

    public VelosoTraversableStack() {
        super();
        cursor = -1;
    }

    @Override
    public T push(T item) {
        T pushedItem = super.push(item);
        cursor = this.size() - 1;
        return pushedItem;
    }

    @Override
    public T pop() {
        T poppedItem = super.pop();
        cursor = this.size() - 1;
        return poppedItem;
    }

    public T peekCursor() {
        if (cursor < 0 || cursor >= this.size()) {
            throw new IllegalStateException("Out of range");
        }
        return this.get(cursor);
    }

    public void top() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Empty stack");
        }
        cursor = this.size() - 1;
    }

    public boolean down() {
        if (cursor <= 0) {
            return false;
        }
        cursor--;
        return true;
    }

    public boolean isCursorAtBottom() {
        return cursor == 0;
    }

    public void reverse() {
        if (this.isEmpty()) return;

        Stack<T> tempStack1 = new Stack<>();
        Stack<T> tempStack2 = new Stack<>();

        while (!this.isEmpty()) {
            tempStack1.push(this.pop());
        }

        while (!tempStack1.isEmpty()) {
            tempStack2.push(tempStack1.pop());
        }

        while (!tempStack2.isEmpty()) {
            this.push(tempStack2.pop());
        }
    }

}