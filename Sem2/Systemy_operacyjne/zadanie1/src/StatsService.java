import java.util.ArrayList;
import java.util.List;

public class StatsService {
    private static final List<Integer> firstExecutionWaitTimes = new ArrayList<>();
    private static final List<Integer> fullExecutionWaitTimes = new ArrayList<>();

    private static int starvedThreshold = Integer.MAX_VALUE;

    private static int starvedProcesses = 0;
    private static int processSwitches = 0;
    private static int simulationTicks = 0;
    private static int emptyTicks = 0;
    private static long executionTime = 0;

    public static void firstExecution(CpuProcess process, int tick) {
        firstExecutionWaitTimes.add(tick - process.getRegisteredAt());

        if (tick - process.getRegisteredAt() > starvedThreshold) {
            starvedProcess();
        }
    }

    public static void fullExecution(CpuProcess process, int tick) {
        fullExecutionWaitTimes.add(tick - process.getRegisteredAt());
    }

    public static void setStarvedThreshold(int threshold) {
        starvedThreshold = threshold;
    }

    public static void emptyTick() {
        emptyTicks++;
    }

    public static void starvedProcess() {
        starvedProcesses++;
    }

    public static void newProcessSwitch() {
        processSwitches++;
    }

    public static void setSimulationTicks(int simulationTicks) {
        StatsService.simulationTicks = simulationTicks;
    }

    public static void setExecutionTime(long executionTime) {
        StatsService.executionTime = executionTime;
    }

    public static void reset() {
        firstExecutionWaitTimes.clear();
        fullExecutionWaitTimes.clear();
        starvedProcesses = 0;
        processSwitches = 0;
        emptyTicks = 0;
        simulationTicks = 0;
    }

    private static double avgWaitTime(List<Integer> data) {
        double avg = 0;
        for (int integer : data) {
            avg += integer;
        }
        return avg / data.size();
    }

    private static int maxWaitTime(List<Integer> data) {
        int max = -1;
        for (int integer : data) {
            max = Math.max(max, integer);
        }
        return max;
    }

    private static String formatExecTime() {
        double millis = executionTime / 1_000_000d;
        if (millis < 1000) return millis + "ms";
        return (millis / 1_000) + "s";
    }

    public static String getStats() {
        double avgFirstExecWaitTime = avgWaitTime(firstExecutionWaitTimes);
        int maxFirstExecWaitTime = maxWaitTime(firstExecutionWaitTimes);

        double avgFullExecWaitTime = avgWaitTime(fullExecutionWaitTimes);
        int maxFullExecWaitTime = maxWaitTime(fullExecutionWaitTimes);

        return "Average wait time for first execution: " + avgFirstExecWaitTime + "\n" +
                "Max wait time for first execution: " + maxFirstExecWaitTime + "\n" +
                "Average wait time for full execution: " + avgFullExecWaitTime + "\n" +
                "Max wait time for full execution: " + maxFullExecWaitTime + "\n" +
                "Starved processes (threshold=" + starvedThreshold + "): " + starvedProcesses + "\n" +
                "Number of process switches: " + processSwitches + "\n" +
                "Number of empty ticks: " + emptyTicks + "\n" +
                "Total simulation ticks: " + simulationTicks + "\n" +
                "Total simulation time: " + formatExecTime() + "\n";
    }
}
