public class CountingSorter {
    public int[] sort(int[] inputArray, int maxValue) {
        // jezeli maxValue to 0 to trzeba +1
        int range = maxValue + 1;
        int arrayLength = inputArray.length;

        int[] countArray = new int[range];
        int[] sortedArray = new int[arrayLength];

        for (int currentElement : inputArray) {
            countArray[currentElement]++;
        }

        // zmiana na tablicę pozycji
        countArray[0]--;
        for (int i = 1; i < range; i++) {
            countArray[i] += countArray[i - 1];
        }

        for (int i = arrayLength - 1; i >= 0; i--) {
            int currentElement = inputArray[i];
            int position = countArray[currentElement];
            sortedArray[position] = currentElement;
            countArray[currentElement]--;
        }

        System.arraycopy(sortedArray, 0, inputArray, 0, arrayLength);

        return inputArray;
    }
}
