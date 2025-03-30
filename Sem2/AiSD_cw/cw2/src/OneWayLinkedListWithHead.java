import java.util.Iterator;

public class OneWayLinkedListWithHead<E> extends AbstractList<E> {
    private class Element {
        private E value;
        private Element next;

        public E getValue() {
            return value;
        }

        public void setValue(E value) {
            this.value = value;
        }

        public Element getNext() {
            return next;
        }

        public void setNext(Element next) {
            this.next = next;
        }

        Element(E data) {
            this.value = data;
        }
    }

    Element head = null;

    public OneWayLinkedListWithHead() {
    }

    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public void clear() {
        head = null;
    }

    @Override
    public int size() {
        int pos = 0;
        Element actElem = head;
        while (actElem != null) {
            pos++;
            actElem = actElem.getNext();
        }
        return pos;
    }

    /**
     * zwraca referencję na Element, wewnętrzną klasę
     */
    private Element getElement(int index) {
        if (index < 0) throw new IndexOutOfBoundsException();
        Element actElem = head;
        while (index > 0 && actElem != null) {
            index--;
            actElem = actElem.getNext();
        }
        if (actElem == null)
            throw new IndexOutOfBoundsException();
        return actElem;
    }

    @Override
    public boolean add(E e) {
        Element newElem = new Element(e);
        if (head == null) {
            head = newElem;
            return true;
        }
        Element tail = head;
        while (tail.getNext() != null)
            tail = tail.getNext();
        tail.setNext(newElem);
        return true;
    }

    @Override
    public void add(int index, E data) {
        if (index < 0) throw new IndexOutOfBoundsException();
        Element newElem = new Element(data);
        if (index == 0) {
            newElem.setNext(head);
            head = newElem;
            return;
        }
        Element actElem = getElement(index - 1);
        newElem.setNext(actElem.getNext());
        actElem.setNext(newElem);
        return;
    }

    @Override
    public int indexOf(E data) {
        int pos = 0;
        Element actElem = head;
        while (actElem != null) {
            if (actElem.getValue().equals(data))
                return pos;
            pos++;
            actElem = actElem.getNext();
        }
        return -1;
    }

    @Override
    public boolean contains(E data) {
        return indexOf(data) >= 0;
    }

    @Override
    public E get(int index) {
        Element actElem = getElement(index);
        return actElem.getValue();
    }

    @Override
    public E set(int index, E data) {
        Element actElem = getElement(index);
        E elemData = actElem.getValue();
        actElem.setValue(data);
        return elemData;
    }

    @Override
    public E remove(int index) {
        if (index < 0 || head == null) throw new IndexOutOfBoundsException();
        if (index == 0) {
            E retValue = head.getValue();
            head = head.getNext();
            return retValue;
        }
        Element actElem = getElement(index - 1);
        if (actElem.getNext() == null)
            throw new IndexOutOfBoundsException();
        E retValue = actElem.getNext().getValue();
        actElem.setNext(actElem.getNext().getNext());
        return retValue;
    }

    @Override
    public boolean remove(E value) {
        if (head == null)
            return false;
        if (head.getValue().equals(value)) {
            head = head.getNext();
            return true;
        }
        Element actElem = head;
        while (actElem.getNext() != null && !actElem.getNext().getValue().equals(value))
            actElem = actElem.getNext();
        if (actElem.getNext() == null)
            return false;
        actElem.setNext(actElem.getNext().getNext());
        return true;
    }

    private class InnerIterator implements Iterator<E> {
        Element actElem;

        public InnerIterator() {
            actElem = head;
        }

        @Override
        public boolean hasNext() {
            return actElem != null;
        }

        @Override
        public E next() {
            E value = actElem.getValue();
            actElem = actElem.getNext();
            return value;
        }

        @Override
        public void remove() {
            if (actElem != null) {
                OneWayLinkedListWithHead.this.remove(actElem.value);
            }
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new InnerIterator();
    }

    @Override
    public ListIterator<E> listIterator() {
        return null;
    }
}