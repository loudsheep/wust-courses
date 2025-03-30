package dsaa.lab04;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Document {
    public String name;
    public TwoWayCycledOrderedListWithSentinel<Link> link;

    public Document(String name, Scanner scan) {
        this.name = name.toLowerCase();
        link = new TwoWayCycledOrderedListWithSentinel<Link>();
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
}

