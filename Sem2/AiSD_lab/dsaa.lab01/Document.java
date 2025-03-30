package dsaa.lab01;

import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Document {
    public static void loadDocument(String name, Scanner scan) {
        while (true) {
            String line = scan.nextLine();

            if (Objects.equals(line, "eod")) break;

            String[] words = line.split("\\s+");

            for (String word : words) {
                if (correctLink(word)) {
                    printMatch(word);
                }
            }
        }
    }

    public static void printMatch(String match) {
        String[] elems = match.split("=");
        if (elems.length == 2) {
            System.out.println(elems[1].toLowerCase());
        }

    }

    // accepted only small letters, capitalic letter, digits nad '_' (but not on the begin)
    public static boolean correctLink(String link) {
        Pattern pattern = Pattern.compile("^[Ll][Ii][Nn][Kk]=[a-zA-Z][a-zA-Z0-9_]*$");
        Matcher matcher = pattern.matcher(link);

        return matcher.find();
    }

}
