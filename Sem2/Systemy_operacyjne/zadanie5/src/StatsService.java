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

        public int getCount() {
            return this.numberOfElements;
        }

        public void reset() {
            numberOfElements = 0;
            sumOfElements = 0;
        }
    }

    private static int utilizationChecks = 0;
    private static int tasksTakenAway = 0;
    private static int taskMigrations = 0;
    private static int tasksNotMigrated = 0;
    private static int tasksFinished = 0;
    private static int emptyTicks = 0;
    private static SumAvgMaxCollector[] cpuUtilizations;
    private static final SumAvgMaxCollector avgUtilization;
    private static final SumAvgMaxCollector delayedTasks;

    static {
        cpuUtilizations = new SumAvgMaxCollector[Settings.NUM_CPUS];
        for (int i = 0; i < Settings.NUM_CPUS; i++) {
            cpuUtilizations[i] = new SumAvgMaxCollector();
        }
        avgUtilization = new SumAvgMaxCollector();
        delayedTasks = new SumAvgMaxCollector();
    }

    public static void taskFinished() {
        tasksFinished++;
    }

    public static void utilizationCheck() {
        utilizationChecks++;
    }

    public static void taskTakenAway() {
        tasksTakenAway++;
    }

    public static void taskMigration() {
        taskMigrations++;
    }

    public static void taskNotMigrated() {
        tasksNotMigrated++;
    }

    public static void reportUtilization(Processor p) {
        cpuUtilizations[p.getPid()].addNumber(p.getCurrentUtilization());
        avgUtilization.addNumber(p.getCurrentUtilization());
    }

    public static void delayedTask(Task task, int tick) {
        delayedTasks.addNumber(tick - task.getArrivalTime());
    }

    public static void emptyTick() {
        emptyTicks++;
    }

    private static double utilStandardDeviation() {
        double sum = 0;
        for (SumAvgMaxCollector cpuUtilization : cpuUtilizations) {
            sum += Math.pow(cpuUtilization.getAvg() - avgUtilization.getAvg(), 2);
        }
        return Math.sqrt(sum / Settings.NUM_CPUS);
    }

    public static void reset() {
        utilizationChecks = 0;
        taskMigrations = 0;
        tasksNotMigrated = 0;
        emptyTicks = 0;
        tasksTakenAway = 0;
        tasksFinished = 0;

        cpuUtilizations = new SumAvgMaxCollector[Settings.NUM_CPUS];
        for (int i = 0; i < Settings.NUM_CPUS; i++) {
            cpuUtilizations[i] = new SumAvgMaxCollector();
        }

        avgUtilization.reset();
        delayedTasks.reset();
    }

    public static String getStats() {
        return "Utilization checks: " + utilizationChecks + "\n" +
                "Task migrations: " + taskMigrations + "\n" +
                "Tasks not migrated: " + tasksNotMigrated + "\n" +
                "Average CPU utilization: " + avgUtilization.getAvg() + "\n" +
                "Average CPU util standard deviation: " + utilStandardDeviation() + "\n" +
                "Empty ticks: " + emptyTicks + "\n" +
                "Num delayed tasks: " + delayedTasks.getCount() + "\n" +
                "Max task delay: " + delayedTasks.getMax() + "\n" +
                "Avg task delay: " + delayedTasks.getAvg() + "\n" +
                "Task taken away: " + tasksTakenAway + "\n"+
                "Tasks finished: " + tasksFinished + "\n"
                ;
    }
}