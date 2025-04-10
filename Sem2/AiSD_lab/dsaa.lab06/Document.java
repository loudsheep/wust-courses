package dsaa.lab06;

import java.util.ListIterator;
import java.util.Scanner;

public class Document{
	public String name;
	public TwoWayCycledOrderedListWithSentinel<Link> link;
	public Document(String name, Scanner scan) {
		this.name=name.toLowerCase();
		link=new TwoWayCycledOrderedListWithSentinel<Link>();
		load(scan);
	}
	public void load(Scanner scan) {
		//TODO
	}
	
	// accepted only small letters, capitalic letter, digits nad '_' (but not on the begin)
	

	public static boolean isCorrectId(String id) {
		//TODO
		return false;
	}

	// accepted only small letters, capitalic letter, digits nad '_' (but not on the begin)
	public static Link createLink(String link) {
		//TODO
		return null;
	}

	@Override
	public String toString() {
		String retStr="Document: "+name;
		//TODO
		return retStr;
	}

	public String toStringReverse() {
		String retStr="Document: "+name;
		ListIterator<Link> iter=link.listIterator();
		while(iter.hasNext())
			iter.next();
		//TODO
		while(iter.hasPrevious()){
		}
		return retStr;
	}
	public int[] getWeights() {
		//TODO
		return null;
	}

	public static void showArray(int[] arr) {
		// TODO
	}

	void bubbleSort(int[] arr) {
		showArray(arr);
		//TODO
	}

	public void insertSort(int[] arr) {
		showArray(arr);
		//TODO
		
	}
	public void selectSort(int[] arr) {
		showArray(arr);
		//TODO
	}
	

	public void iterativeMergeSort(int[] arr) {
		showArray(arr);
		//TODO
	}

	public void radixSort(int[] arr) {
		showArray(arr);
		//TODO
	}


}
