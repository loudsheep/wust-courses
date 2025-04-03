package dsaa.lab05;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class TwoWayCycledOrderedListWithSentinel<E extends Comparable<E>> implements IList<E> {

    private class Element {
        E value;
        Element next;
        Element prev;


        public Element(E e) {
            this(e, null, null);
        }

        public Element(E e, Element next, Element prev) {
            this.value = e;
            this.next = next;
            this.prev = prev;
        }

        // add element e after this
        public void addAfter(Element elem) {
            elem.next = this.next;
            this.next.prev = elem;
            this.next = elem;
            elem.prev = this;
        }

        public void addBefore(Element elem) {
            elem.prev = this.prev;
            this.prev.next = elem;
            this.prev = elem;
            elem.next = this;
        }

        // assert it is NOT a sentinel
        public void remove() {
            assert this != sentinel;

            if (next != null) {
                this.next.prev = this.prev;
            }
            if (prev != null) {
                this.prev.next = this.next;
            }
        }
    }

    Element sentinel;
    int size;

    private class InnerIterator implements Iterator<E> {
        Element current;

        public InnerIterator() {
            this.current = sentinel;
        }

        @Override
        public boolean hasNext() {
            return current.next != null && current.next != sentinel;
        }

        @Override
        public E next() {
            current = current.next;
            return current.value;
        }
    }

    private class InnerListIterator implements ListIterator<E> {
        Element current;

        public InnerListIterator() {
            current = sentinel;
        }

        @Override
        public boolean hasNext() {
            return current.next != null && current.next != sentinel;
        }

        @Override
        public E next() {
            current = current.next;
            return current.value;
        }

        @Override
        public void add(E arg0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasPrevious() {
            return current.prev != null && current != sentinel;
        }

        @Override
        public int nextIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public E previous() {
            E toRet = current.value;
            current = current.prev;
            return toRet;
        }

        @Override
        public int previousIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(E arg0) {
            throw new UnsupportedOperationException();
        }
    }

    public TwoWayCycledOrderedListWithSentinel() {
        this.clear();
    }

    //@SuppressWarnings("unchecked")
    @Override
    public boolean add(E e) {
        if (size == 0) {
            this.sentinel.addAfter(new Element(e));
            size++;
            return true;
        }

        Element tmp = sentinel;
        while (tmp.next != null && tmp.next != sentinel) {
            if (tmp.next.value.compareTo(e) > 0) {
                break;
            }
            tmp = tmp.next;
        }

        tmp.addAfter(new Element(e));
        size++;

        return true;
    }

    private Element getElement(int index) {
        if (index < 0 || index >= size) throw new NoSuchElementException();

        Element tmp = sentinel.next;
        while (index > 0 && tmp != null && tmp != this.sentinel) {
            index--;
            tmp = tmp.next;
        }

        if (index > 0 || tmp == null) throw new NoSuchElementException();

        return tmp;
    }

    private Element getElement(E obj) {
        Element tmp = sentinel.next;
        while (tmp.next != null && tmp != this.sentinel) {
            if (tmp.value.equals(obj)) return tmp;
            tmp = tmp.next;
        }
        return null;
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        this.sentinel = new Element(null);
        this.sentinel.next = this.sentinel;
        this.sentinel.prev = this.sentinel;
        this.size = 0;
    }

    @Override
    public boolean contains(E element) {
        return this.indexOf(element) >= 0;
    }

    @Override
    public E get(int index) {
        return this.getElement(index).value;
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(E element) {
        Element tmp = sentinel.next;
        int pos = 0;
        while (tmp != null && tmp != sentinel) {
            if (tmp.value.equals(element)) return pos;
            tmp = tmp.next;
            pos++;
        }

        return -1;
    }

    @Override
    public boolean isEmpty() {
        return size <= 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new InnerIterator();
    }

    @Override
    public ListIterator<E> listIterator() {
        return new InnerListIterator();
    }

    @Override
    public E remove(int index) {
        Element tmp = getElement(index);
        tmp.remove();
        size--;
        return tmp.value;
    }

    @Override
    public boolean remove(E e) {
        Element tmp = getElement(e);
        if (tmp == null) return false;

        tmp.remove();
        size--;
        return true;
    }

    @Override
    public int size() {
        return size;
    }

    //@SuppressWarnings("unchecked")
    public void add(TwoWayCycledOrderedListWithSentinel<E> other) {
        if (other == this || other.isEmpty()) return;
        if (isEmpty()) {
            this.sentinel = other.sentinel;
            this.size = other.size;
            other.clear();
            return;
        }


        Element thisElem = sentinel.next;
        Element otherElem = other.sentinel.next;

        while (thisElem != null && thisElem != this.sentinel && otherElem != null && otherElem != other.sentinel) {
            // if this <= other -> skip this
            // if this > other -> insert other before this, skip this, skip other
            if (thisElem.value.compareTo(otherElem.value) <= 0) {
                thisElem = thisElem.next;
            } else {
                Element next = otherElem.next;
                thisElem.addBefore(otherElem);
                otherElem = next;
            }
        }

        thisElem = thisElem.prev;
        while (otherElem != null && otherElem != other.sentinel) {
            Element next = otherElem.next;
            thisElem.addAfter(otherElem);
            thisElem = thisElem.next;
            otherElem = next;
        }

        this.size += other.size;
        other.clear();
    }

    //@SuppressWarnings({ "unchecked", "rawtypes" })
    public void removeAll(E e) {
        Element tmp = sentinel.next;

        boolean deleted = false;
        while (tmp != null && tmp != sentinel) {
            if (tmp.value.equals(e)) {
                tmp.remove();
                size--;
                deleted = true;
            } else if (deleted) {
                break;
            }
            tmp = tmp.next;
        }
    }
}