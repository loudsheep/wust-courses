import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        AbstractList<Integer> list = new OneWayLinkedListWithHead<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(8);


        System.out.println(list);

        Iterator<Integer> iter = list.iterator();

        while(iter.hasNext()) {
            int n = iter.next();
            if (n % 2 == 0) iter.remove();
            System.out.println(n);
        }

        System.out.println(list);
    }
}