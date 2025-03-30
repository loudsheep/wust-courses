package dsaa.lab03;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;


public class TwoWayUnorderedListWithHeadAndTail<E> implements IList<E> {

    private class Element {
        public Element(E e) {
            this.value = e;
        }

        public Element(E e, Element next, Element prev) {
            this.value = e;
            this.next = next;
            this.prev = prev;
        }

        E value;
        Element next = null;
        Element prev = null;

        public Element getNext() {
            return next;
        }

        public void setNext(Element next) {
            this.next = next;
        }

        public Element getPrev() {
            return prev;
        }

        public void setPrev(Element prev) {
            this.prev = prev;
        }

        public void insertAfter(Element elem) {
            elem.setNext(this.getNext());
            elem.setPrev(this);
            if (this.getNext() != null)
                this.getNext().setPrev(elem);
            this.setNext(elem);
        }

        public void insertBefore(Element elem) {
            elem.setNext(this);
            elem.setPrev(this.getPrev());
            if (this.getPrev() != null)
                this.getPrev().setNext(elem);
            this.setPrev(elem);
        }

        public void remove() {
            if (next != null)
                this.getNext().setPrev(this.getPrev());
            if (prev != null)
                this.getPrev().setNext(this.getNext());
        }
    }

    Element head;
    Element tail;
    // can be realization with the field size or without
    int size;

    private class InnerIterator implements Iterator<E> {
        Element pos;

        public InnerIterator() {
            pos = head;
        }

        @Override
        public boolean hasNext() {
            return pos != null;
        }

        @Override
        public E next() {
            E retVal = pos.value;
            pos = pos.next;
            return retVal;
        }
    }

    private class InnerListIterator implements ListIterator<E> {
        int index = 0;
        boolean wasNext = false;
        boolean wasPrevious = false;
        Element next;
        Element prev;

        public InnerListIterator() {
            super();
            next = head;
            prev = null;
        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public boolean hasPrevious() {
            return prev != null;
        }

        @Override
        public E next() {
            Element current = next;
            next = next.next;
            prev = current;
            index++;
            wasNext = true;
            return current.value;
        }

        @Override
        public int nextIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public E previous() {
            Element current = prev;
            prev = prev.prev;
            next = current;
            index--;
            wasPrevious = true;
            return current.value;
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
        public void set(E e) {
            if (wasNext) {
                prev.value = e;
            } else if (wasPrevious) {
                next.value = e;
            } else {
                throw new IllegalStateException();
            }
        }
    }

    public TwoWayUnorderedListWithHeadAndTail() {
        // make a head and a tail
        head = null;
        tail = null;
        size = 0;
    }

    private Element getElement(int index) {
        if (index < 0 || size == 0 || index > size) throw new NoSuchElementException();

        Element tmp = head;
        while (index > 0 && tmp != null) {
            index--;
            tmp = tmp.getNext();
        }

        if (index > 0 || tmp == null) throw new NoSuchElementException();

        return tmp;
    }

    private Element getElement(E value) {
        Element tmp = head;
        while (tmp != null) {
            if (tmp.value.equals(value)) return tmp;
            tmp = tmp.next;
        }
        return null;
    }

    @Override
    public boolean add(E e) {
        if (size == 0) {
            head = new Element(e);
        } else if (size == 1) {
            tail = new Element(e, null, head);
            head.next = tail;
        }else{
            tail.next = new Element(e, null, tail);
            tail = tail.next;
        }
        size++;

        return true;
    }

    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size) throw new NoSuchElementException();

        if (index == size) {
            add(element);
            return;
        }

        if (index == 0) {
            Element newElem = new Element(element, head, null);
            head = newElem;
        } else {
            Element newElem = new Element(element);
            Element tmp = getElement(index);
            tmp.insertBefore(newElem);
        }

        size++;
    }

    @Override
    public void clear() {
        this.head = null;
        this.tail = null;
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
        Element elem = this.getElement(index);
        E retVal = elem.value;
        elem.value = element;
        return retVal;
    }

    @Override
    public int indexOf(E element) {
        int pos = 0;
        Element tmp = head;
        while (tmp != null) {
            if (tmp.value.equals(element)) return pos;
            tmp = tmp.getNext();
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
        Element elem = this.getElement(index);
        elem.remove();
        size--;
        if (index == 0) {
            head = elem.next;
        } else if (index == size) {
            tail = elem.prev;
        }
        return elem.value;
    }

    @Override
    public boolean remove(E e) {
        Element elem = getElement(e);
        if (elem == null) return false;

        elem.remove();
        if (elem == head) {
            head = elem.next;
        } else if (elem == tail) {
            tail = elem.prev;
        }
        size--;
        return true;
    }

    @Override
    public int size() {
        return size;
    }

    public void add(TwoWayUnorderedListWithHeadAndTail<E> other) {
        if (this == other || other.isEmpty()) return;

        if (this.head == null) {
            this.head = other.head;
            this.tail = other.tail;
            this.size = other.size;
            other.clear();
        } else {
            this.tail.setNext(other.head);
            other.head.setPrev(this.tail);
            this.tail = other.tail;
            this.size += other.size;
            other.clear();
        }
    }

    public void removeDuplicates() {
        Element tmp = head;
        E lastValue = null;
        while(tmp != null) {
            if (lastValue != null && lastValue.equals(tmp.value)) {
                tmp.remove();
                if (tmp == tail) {
                    tail = tmp.prev;
                }
                size--;
            } else {
                lastValue = tmp.value;
            }

            tmp = tmp.next;
        }
    }
}

