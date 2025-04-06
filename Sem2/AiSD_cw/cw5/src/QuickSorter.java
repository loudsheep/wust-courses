import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class QuickSorter<T> implements ListSorter<T> {
    private final Comparator<T> _comparator;
    private final Random rand;

    public QuickSorter(Comparator<T> comparator) {
        _comparator = comparator;
        rand = new Random();
    }

    private void swapElements(List<T> list, int idx1, int idx2) {
        if (idx1 == idx2) return;

        T tmp = list.get(idx1);
        list.set(idx1, list.get(idx2));
        list.set(idx2, tmp);
    }

    private T selectPivot(List<T> list, int left, int right, Random rand) {
        int size = right - left + 1;

        if (size > 100) {
            int i1 = left + rand.nextInt(size);
            int i2 = left + rand.nextInt(size);
            int i3 = left + rand.nextInt(size);

            List<T> sample = Arrays.asList(list.get(i1), list.get(i2), list.get(i3));
            sample.sort(_comparator);
            return sample.get(1); // median of three
        } else {
            return list.getLast();
        }
    }

    // private static <T extends Comparable<T>> void quickSort(List<T> list, int low, int high, Random rand) {
    //        if (low >= high) return;
    //
    //        T pivot = selectPivot(list, low, high, rand);
    //
    //        int i = low, j = high;
    //
    //        while (i <= j) {
    //            while (list.get(i).compareTo(pivot) < 0) i++;
    //            while (list.get(j).compareTo(pivot) > 0) j--;
    //            if (i <= j) {
    //                Collections.swap(list, i, j);
    //                i++;
    //                j--;
    //            }
    //        }
    //
    //        if (low < j) quickSort(list, low, j, rand);
    //        if (i < high) quickSort(list, i, high, rand);
    //    }

    private void quickSort(List<T> list, int left, int right, Random random) {
        if (left >= right) return;

        T picot = selectPivot(list, left, right, random);
        int i = left + 1, j = right - 1;

        while (i <= j) {
            while (i <= j && _comparator.compare(list.get(i), picot) <= 0) i++;
            while (_comparator.compare(list.get(j), picot) > 0) j--;

//            if (i <= j) {
//                swapElements(list, i, j);
//                i++;
//                j--;
//            }
            if (i < j) {
                swapElements(list, i, j);
                i++;
                j--;
            }
        }

        if (left < j) quickSort(list, left, j, random);
        if (i < right) quickSort(list, i, right, random);
    }

//    private int partition(List<T> list, int nFrom, int nTo) {

    /// /jako element dzielący bierzemy losowy
//        int rnd = nFrom + rand.nextInt(nTo - nFrom);
//        swapElements(list, nFrom, rnd);
//        T value = list.get(nFrom);
//        int idxBigger = nFrom + 1, idxLower = nTo - 1;
//        do {
//            while (idxBigger <= idxLower && _comparator.compare(list.get(idxBigger), value) <= 0)
//                idxBigger++;
//            while (_comparator.compare(list.get(idxLower), value) > 0)
//                idxLower--;
//            if (idxBigger < idxLower)
//                swapElements(list, idxBigger, idxLower);
//        } while (idxBigger < idxLower);
//        swapElements(list, idxLower, nFrom);
//        return idxLower;
//    }
//
//    private void quickSort(List<T> list, int startIndex, int endIndex) {
//        if (endIndex - startIndex > 1) {
//            int partition = partition(list, startIndex, endIndex);
//            quickSort(list, startIndex, partition);
//            quickSort(list, partition + 1, endIndex);
//        }
//    }
    public List<T> sort(List<T> list) {
        quickSort(list, 0, list.size(), rand);
        return list;
    }
}
