package zadanie3;

public class StatsService {
    private static int pageFaultCount = 0;
    private static int requestsCount = 0;
    private static long executionTime = 0;

    public static void pageFault() {
        pageFaultCount++;
        requestsCount++;
    }

    public static void readPage() {
        requestsCount++;
    }

    public static void reset() {
        pageFaultCount = 0;
        requestsCount = 0;
        executionTime = 0;
    }

    public static void setExecutionTime(long executionTime) {
        StatsService.executionTime = executionTime;
    }

    private static String formatExecTime() {
        double millis = executionTime / 1_000_000d;
        if (millis < 1000) return millis + "ms";
        return (millis / 1_000) + "s";
    }

    public static void showStats() {
        String res = "Page fault count = " + pageFaultCount + "\n" +
                "Requests processed = " + requestsCount + "\n" +
                "Total simulation time: " + formatExecTime() + "\n";

        System.out.println(res);
    }
}
