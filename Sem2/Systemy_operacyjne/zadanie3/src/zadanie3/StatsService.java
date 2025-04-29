package zadanie3;

import java.util.LinkedList;
import java.util.Queue;

public class StatsService {
    private static int pageFaultCount = 0;
    private static int requestsCount = 0;
    private static long executionTime = 0;
    private static int thrashingCount = 0;

    private static int THRASHING_PERIOD = 10;
    private static int THRASHING_THRESHOLD = 5;

    private static Queue<Boolean> recentRequests = new LinkedList<>();
    private static int faultsInPeriod = 0;

    public static void pageFault() {
        pageFaultCount++;
        trackRequest(true);
        pageRead(); // still counts as a request
    }

    public static void pageRead() {
        requestsCount++;
        trackRequest(false);
    }

    private static void trackRequest(boolean isPageFault) {
        if (recentRequests.size() == THRASHING_PERIOD) {
            boolean removed = recentRequests.poll();
            if (removed) {
                faultsInPeriod--;
            }
        }

        recentRequests.offer(isPageFault);
        if (isPageFault) {
            faultsInPeriod++;
        }

        if (recentRequests.size() == THRASHING_PERIOD && faultsInPeriod >= THRASHING_THRESHOLD) {
            thrashingCount++;
            recentRequests.clear(); // reset tracking after detecting thrashing
            faultsInPeriod = 0;
        }
    }

    public static void reset() {
        pageFaultCount = 0;
        requestsCount = 0;
        executionTime = 0;
        thrashingCount = 0;
        recentRequests.clear();
        faultsInPeriod = 0;
    }

    public static void setExecutionTime(long executionTime) {
        StatsService.executionTime = executionTime;
    }

    public static void setThrashingPeriod(int period) {
        THRASHING_PERIOD = period;
        recentRequests.clear();
        faultsInPeriod = 0;
    }

    public static void setThrashingThreshold(int threshold) {
        THRASHING_THRESHOLD = threshold;
    }

    private static String formatExecTime() {
        double millis = executionTime / 1_000_000d;
        if (millis < 1000) return millis + "ms";
        return (millis / 1_000) + "s";
    }

    public static void showStats() {
        String res = "Page fault count = " + pageFaultCount + "\n" +
                "Requests processed = " + requestsCount + "\n" +
                "Total simulation time: " + formatExecTime() + "\n" +
                "Thrashing periods detected = " + thrashingCount + "\n";

        System.out.println(res);
    }
}
