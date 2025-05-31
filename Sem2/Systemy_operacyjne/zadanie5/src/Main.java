public class Main {

    private static void execute(LoadBalancingStrategy strategy, TaskGenerator generator) {
        generator.reset();

        while (generator.hasTasksLeft() || strategy.hasTasksLeft()) {
            generator.checkForNewTask(strategy);
            strategy.tick();

            if (Math.random() <= 0.005) System.out.println(strategy);
        }
    }

    public static void main(String[] args) {
        TaskGenerator generator = TaskGenerator.fromRandom(Settings.NUM_CPUS, Settings.TASK_COUNT, Settings.MAX_ARRIVAL, Settings.MIN_EXEC, Settings.MAX_EXEC, Settings.MIN_UTIL, Settings.MAX_UTIL);

        LoadBalancingStrategy strategy1 = new Strategy1(Settings.NUM_CPUS);
        execute(strategy1, generator);

        System.out.println(strategy1);
    }
}