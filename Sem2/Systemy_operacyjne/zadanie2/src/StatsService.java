public class StatsService {
    private static class SumAvgMaxCollector {
        private int numberOfElements = 0;
        private long sumOfElements = 0;
        private int maxNumber = Integer.MIN_VALUE;

        public SumAvgMaxCollector() {
        }

        public void addNumber(int num) {
            numberOfElements++;
            sumOfElements += num;
            maxNumber = Math.max(maxNumber, num);
        }

        public long getSum() {
            return sumOfElements;
        }

        public double getAvg() {
            return (double) sumOfElements / numberOfElements;
        }

        public int getMax() {
            return maxNumber;
        }

        public void reset() {
            numberOfElements = 0;
            sumOfElements = 0;
        }
    }

    private static int blockReads = 0;
    private static long executionTime = 0;
    private static long simulationTicks = 0;
    private static int headMovements = 0;
    private static int emptyTicks = 0;
    private static int headJumpsToBeginning = 0;
    private static int realtimeSuccess = 0;
    private static int realtimeStarved = 0;
    private static int starvedRequests = 0;
    private static SumAvgMaxCollector waitTimeCollector = new SumAvgMaxCollector();
    private static SumAvgMaxCollector waitTimeCollectorRealtime = new SumAvgMaxCollector();

    private static int starvedThreshold = Integer.MAX_VALUE;

    public static void blockRead() {
        blockReads++;
    }

    public static void emptyTick() {
        emptyTicks++;
    }

    public static void starvedRequest() {
        starvedRequests++;
    }

    public static void setSimulationTicks(int simulationTicks) {
        StatsService.simulationTicks = simulationTicks;
    }

    public static void setExecutionTime(long executionTime) {
        StatsService.executionTime = executionTime;
    }

    public static void headMoved() {
        headMovements++;
    }

    public static void headJumpsToBeginning() {
        headJumpsToBeginning++;
    }

    public static void requestExecuted(DiscRequest request, int tick) {
        int execTime = tick - request.getArrivalTime();
        waitTimeCollector.addNumber(execTime);

        if (execTime >= starvedThreshold) {
            starvedRequest();
        }
    }

    public static void RTrequestExecuted(DiscRequest request, int tick) {
        if (request.getStatus() == DiscRequest.Status.RT_SUCCESS) realtimeSuccess++;
        else realtimeStarved++;

        waitTimeCollectorRealtime.addNumber(tick - request.getArrivalTime());
    }

    public static void setStarvedThreshold(int starvedThreshold) {
        StatsService.starvedThreshold = starvedThreshold;
    }

    public static void reset() {
        blockReads = 0;
        executionTime = 0;
        simulationTicks = 0;
        headMovements = 0;
        emptyTicks = 0;
        headJumpsToBeginning = 0;
        realtimeSuccess = 0;
        realtimeStarved = 0;
        starvedRequests = 0;
        waitTimeCollector.reset();
        waitTimeCollectorRealtime.reset();
    }

    private static String formatExecTime() {
        double millis = executionTime / 1_000_000d;
        if (millis < 1000) return millis + "ms";
        return (millis / 1_000) + "s";
    }

    public static String getStats() {
        return "Block reads: " + blockReads + "\n" +
                "Head movement sum: " + headMovements + "\n" +
                "Average wait time: " + waitTimeCollector.getAvg() + "\n" +
                "Max wait time: " + waitTimeCollector.getMax() + "\n" +
                "Starved requests(" + starvedThreshold + "): " + starvedRequests + "\n" +
                "Empty ticks: " + emptyTicks + "\n" +
                "Head jumps to beginning: " + headJumpsToBeginning + "\n" +
                "Realtime requests successful: " + realtimeSuccess + "\n" +
                "Realtime requests starved: " + realtimeStarved + "\n" +
                "Realtime requests avg wait time: " + waitTimeCollectorRealtime.getAvg() + "\n" +
                "Realtime requests max wait time: " + waitTimeCollectorRealtime.getMax() + "\n" +
                "Total simulation ticks: " + simulationTicks + "\n" +
                "Total simulation time: " + formatExecTime() + "\n"
                ;
    }
}
