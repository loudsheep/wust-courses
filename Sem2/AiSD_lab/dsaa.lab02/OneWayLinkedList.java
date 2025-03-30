package dsaa.lab02;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class OneWayLinkedList<E> implements IList<E> {

    private class Element {
        public Element(E e) {
            this.object = e;
        }

        E object;
        Element next = null;
    }

    Element sentinel;

    private class InnerIterator implements Iterator<E> {
        Element tmp;

        public InnerIterator() {
            tmp = sentinel;
        }

        @Override
        public boolean hasNext() {
            return tmp.next != null;
        }

        @Override
        public E next() {
            E ret = tmp.next.object;
            tmp = tmp.next;
            return ret;
        }
    }

    public OneWayLinkedList() {
        // make a sentinel
        sentinel = new Element(null);
    }

    @Override
    public Iterator<E> iterator() {
        return new InnerIterator();
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(E e) {
        Element tmp = sentinel;
        while (tmp.next != null) {
            tmp = tmp.next;
        }

        tmp.next = new Element(e);

        return true;
    }

    @Override
    public void add(int index, E element) throws NoSuchElementException {
        if (index < 0) throw new NoSuchElementException();

        Element tmp;
        if (index == 0) tmp = sentinel;
        else tmp = getElement(index - 1);

        Element newElem = new Element(element);
        newElem.next = tmp.next;
        tmp.next = newElem;
    }

    @Override
    public void clear() {
        sentinel.next = null;
    }

    @Override
    public boolean contains(E element) {
        return indexOf(element) >= 0;
    }

    private Element getElement(int index) {
        if (index < 0) throw new NoSuchElementException();

        Element tmp = sentinel.next;
        while (index > 0 && tmp.next != null) {
            index--;
            tmp = tmp.next;
        }

        if (tmp == null || index > 0) throw new NoSuchElementException();

        return tmp;
    }

    @Override
    public E get(int index) throws NoSuchElementException {
        return this.getElement(index).object;
    }

    @Override
    public E set(int index, E element) throws NoSuchElementException {
        Element elem = getElement(index);
        E data = elem.object;
        elem.object = element;

        return data;
    }

    @Override
    public int indexOf(E element) {
        int pos = 0;
        Element tmp = sentinel;
        while (tmp.next != null) {
            if (tmp.next.object.equals(element)) {
                return pos;
            }
            tmp = tmp.next;
            pos++;
        }

        return -1;
    }

    @Override
    public boolean isEmpty() {
        return sentinel.next == null;
    }

    @Override
    public E remove(int index) throws NoSuchElementException {
        if (index < 0 || sentinel.next == null) throw new NoSuchElementException();
        if (index == 0) {
            E removed = sentinel.next.object;
            sentinel.next = sentinel.next.next;
            return removed;
        }

        Element tmp = getElement(index - 1);
        if (tmp.next == null) throw new NoSuchElementException();

        E removed = tmp.next.object;
        tmp.next = tmp.next.next;
        return removed;
    }

    @Override
    public boolean remove(E e) {
        if (sentinel.next == null) return false;

        Element tmp = sentinel;
        while (tmp.next != null && !tmp.next.object.equals(e)) {
            tmp = tmp.next;
        }

        if (tmp.next == null) return false;

        tmp.next = tmp.next.next;

        return true;
    }

    @Override
    public int size() {
        if (sentinel.next == null) return 0;
        Element tmp = sentinel;
        int count = 0;
        while (tmp.next != null) {
            count++;
            tmp = tmp.next;
        }
        return count;
    }

    public void removeOddElements() {
        if (this.isEmpty()) return;

        int size = this.size();
        int i = 0;
        Element current = this.sentinel;
        while(i < size) {
            current.next = current.next.next;
            current = current.next;
            i++;
            size--;
        }
    }
}

