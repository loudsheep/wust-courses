package dsaa.lab02;

import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Document {
    public String name;
    public OneWayLinkedList<Link> links;

    public Document(String name, Scanner scan) {
		this.name = name;
		this.links = new OneWayLinkedList<>();
        load(scan);
    }

	private static String printMatch(String match) {
		String[] elems = match.split("=");
		if (elems.length == 2) {
			return elems[1].toLowerCase();
		}
		return null;
	}

	public void load(Scanner scan) {
		while (true) {
			String line = scan.nextLine();

			if (Objects.equals(line, "eod")) break;

			String[] words = line.split("\\s+");

			for (String word : words) {
				if (correctLink(word)) {
//					printMatch(word);
					links.add(new Link(printMatch(word)));
				}
			}
		}
	}

	// accepted only small letters, capitalic letter, digits nad '_' (but not on the begin)
	public static boolean correctLink(String link) {
		Pattern pattern = Pattern.compile("^[Ll][Ii][Nn][Kk]=[a-zA-Z][a-zA-Z0-9_]*$");
		Matcher matcher = pattern.matcher(link);

		return matcher.find();
	}

    @Override
    public String toString() {
		String res = "Document: " + name;

		Iterator<Link> iter = links.iterator();
		while(iter.hasNext()) {
			res += "\n" + iter.next().ref;
		}

		return res;
    }

}
