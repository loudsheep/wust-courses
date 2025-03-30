import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> numbers2 = Arrays.asList(10, 9, 8, 7, 6);
//        Iterator<Integer> iterator = new KthIterator<>(numbers.iterator(), 3);

//        FibbonacciGenerator iterator = new FibbonacciGenerator();
//        NextNumberGenerator iterator = new NextNumberGenerator(100);
//        ShuffleIterator<Integer> iterator = new ShuffleIterator<>(numbers2.iterator(), numbers.iterator());
//        PrimeGenerator iterator = new PrimeGenerator(200);
//
        Integer[] f = new Integer[]{3, 1, 5, 7, 8, 9, 4};

//        ArrayIterator<Integer> iterator = new ArrayIterator<>(f);
//        iterator.remove();


        Integer[][] matrix = {
                {1, 2, 3, 33},
                {},
                {4, 5, 6},
                {7, 8, 9, 77, 88, 99},
                {}
        };

        Array2DIterator<Integer> iterator = new Array2DIterator<>(matrix);
//        Array2DReverseIterator<Integer> iterator = new Array2DReverseIterator<>(matrix);

        int i = 0;
        while (iterator.hasNext() && i < 100) {
            System.out.println(iterator.next());

            i++;
        }
    }
}