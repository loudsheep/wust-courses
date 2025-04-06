public class StatsService {
    private static class SumAvgMaxCollector {
        private int numberOfElements = 0;
        private int sumOfElements = 0;
        private int maxNumber = Integer.MIN_VALUE;

        public SumAvgMaxCollector() {
        }

        public void addNumber(int num) {
            numberOfElements++;
            sumOfElements += num;
            maxNumber = Math.max(maxNumber, num);
        }

        public int getSum() {
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
    private static SumAvgMaxCollector waitTimeCollector = new SumAvgMaxCollector();

    public static void blockRead() {
        blockReads++;
    }

    public static void emptyTick() {
        emptyTicks++;
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

    public static void requestExecuted(DiscRequest request, int tick) {
        waitTimeCollector.addNumber(tick - request.getArrivalTime());
    }

    public static void reset() {
        blockReads = 0;
        executionTime = 0;
        simulationTicks = 0;
        headMovements = 0;
        emptyTicks = 0;
        waitTimeCollector.reset();
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
                "Empty ticks: " + emptyTicks + "\n" +
                "Total simulation ticks: " + simulationTicks + "\n" +
                "Total simulation time: " + formatExecTime() + "\n"
                ;
    }
}
