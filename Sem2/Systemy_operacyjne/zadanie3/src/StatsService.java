public class StatsService {
    private static int pageFaultCount = 0;

    public static void pageFault() {
        pageFaultCount++;
    }

    public static void reset() {
        pageFaultCount = 0;
    }

    public static void showStats() {
        String res = "Page fault count = " + pageFaultCount + "\n";

        System.out.println(res);
    }
}
