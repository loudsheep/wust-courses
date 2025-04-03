package dsaa.lab05;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Document {
    public String name;
    public TwoWayCycledOrderedListWithSentinel<Link> link;

    public Document(String name, Scanner scan) {
        this.name = name.toLowerCase();
        link = new TwoWayCycledOrderedListWithSentinel<>();
        load(scan);
    }

    public void load(Scanner scan) {
        while (true) {
            String line = scan.nextLine();

            if (Objects.equals(line, "eod")) break;

            String[] words = line.split("\\s+");

            for (String word : words) {
                if (correctLink(word)) {
                    String[] split = word.split("=");
                    Link tmp = createLink(split[1]);

                    if (tmp != null) {
                        link.add(tmp);
                    }
                }
            }
        }
    }

    public static boolean correctLink(String link) {
        Pattern pattern = Pattern.compile("^[Ll][Ii][Nn][Kk]=[a-zA-Z][a-zA-Z0-9_]*([(]\\d+[)])?$");
        Matcher matcher = pattern.matcher(link);

        return matcher.matches();
    }

    public static boolean isCorrectId(String id) {
        Pattern pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$");
        Matcher matcher = pattern.matcher(id);

        return matcher.matches();
    }

    // accepted only small letters, capitalic letter, digits nad '_' (but not on the begin)
    public static Link createLink(String link) {
        Pattern pattern = Pattern.compile("^([a-zA-Z][a-zA-Z0-9_]*)([(]\\d+[)])?$");
        Matcher matcher = pattern.matcher(link);

        if (!matcher.matches()) return null;

        String[] firstSplit = link.split("[(]");
        String ref = firstSplit[0].toLowerCase();
        if (firstSplit.length > 1) {
            String[] secondSplit = firstSplit[1].split("[)]");
            int weight = Integer.parseInt(secondSplit[0]);

            if (weight <= 0) return null;

            return new Link(ref, weight);
        }
        return new Link(ref);
    }

    @Override
    public String toString() {
        String retStr = "Document: " + name;
        Iterator<Link> iter = link.iterator();
        int i = 0;
        while (iter.hasNext() && i < 10) {
            if (i == 0) retStr += "\n";
            else retStr += " ";

            retStr += iter.next();
            i++;
        }

        i = 0;
        while (iter.hasNext()) {
            if (i == 0) retStr += "\n";
            else retStr += " ";

            retStr += iter.next();
            i++;
        }

        return retStr;
    }

    public String toStringReverse() {
        ListIterator<Link> iter = link.listIterator();
        while (iter.hasNext())
            iter.next();


        String retStr = "Document: " + name;
        int i = 0;
        while (iter.hasPrevious() && i < 10) {
            if (i == 0) retStr += "\n";
            else retStr += " ";

            retStr += iter.previous();
            i++;
        }

        i = 0;
        while (iter.hasPrevious()) {
            if (i == 0) retStr += "\n";
            else retStr += " ";

            retStr += iter.previous();
            i++;
        }

        return retStr;
    }

    public int[] getWeights() {
        int[] weights = new int[link.size()];

        Iterator<Link> iter = link.iterator();
        int i = 0;
        while (iter.hasNext()) {
            weights[i] = iter.next().weight;
            i++;
        }

        return weights;
    }

    public static void showArray(int[] arr) {
        String res = Arrays.stream(arr)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(" "));

        System.out.println(res);
    }

    private static void swapElements(int[] arr, int l, int r) {
        int tmp = arr[l];
        arr[l] = arr[r];
        arr[r] = tmp;
    }

    void bubbleSort(int[] arr) {
        showArray(arr);

        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = arr.length - 1; j > i ; j--) {
                if (arr[j - 1] > arr[j]) {
                    swapElements(arr, j, j-1);
                }
            }
            showArray(arr);
        }
    }

    public void insertSort(int[] arr) {
        showArray(arr);

        for (int i = arr.length - 2; i >= 0; i--) {
            int value = arr[i];
            int tmp;
            int j = i;
            for (; j < arr.length - 1; j++) {
                tmp = arr[j + 1];
                if (value < arr[j + 1]) break;

                arr[j] = tmp;
            }

            arr[j] = value;
            showArray(arr);
        }
    }

    public void selectSort(int[] arr) {
        showArray(arr);

        for (int i = 0; i < arr.length - 1; i++) {
            int minPos = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[minPos]) {
                    minPos = j;
                }
            }
            swapElements(arr, i, minPos);
            showArray(arr);
        }
    }

}
