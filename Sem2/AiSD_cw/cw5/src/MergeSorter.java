import java.util.*;

public class MergeSorter<T>{
    private final Comparator<T> _comparator;

    public MergeSorter(Comparator<T> comparator) {
        _comparator = comparator;
    }

    private LinkedList<T> merge(LinkedList<T> a, LinkedList<T> b) {
        LinkedList<T> result = new LinkedList<>();

        while (!a.isEmpty() && !b.isEmpty()) {
            if (_comparator.compare(a.peekFirst(), b.peekFirst()) <= 0) {
                result.add(a.pollFirst());
            } else {
                result.add(b.pollFirst());
            }
        }

        result.addAll(a);
        result.addAll(b);

        return result;
    }

    private void split(LinkedList<T> source, LinkedList<T> left, LinkedList<T> right) {
        int mid = source.size() / 2;
        Iterator<T> it = source.iterator();
        for (int i = 0; i < mid; i++) left.add(it.next());
        while (it.hasNext()) right.add(it.next());
    }

    public LinkedList<T> sort(LinkedList<T> list) {
        if (list == null || list.size() <= 1) return list;

        Queue<LinkedList<T>> listQueue = new LinkedList<>();
        listQueue.add(list);

        while(!listQueue.isEmpty()) {
            LinkedList<T> current = listQueue.poll();

            if (current.size() <= 1) {
                listQueue.add(current);
                break;
            }

            LinkedList<T> left = new LinkedList<>();
            LinkedList<T> right = new LinkedList<>();
            split(current, left, right);

            listQueue.add(left);
            listQueue.add(right);
        }

        while (listQueue.size() > 1) {
            LinkedList<T> first = listQueue.poll();
            LinkedList<T> second = listQueue.poll();

            LinkedList<T> merged = merge(first, second);
            listQueue.add(merged);
        }

        return listQueue.poll();
    }
}
