public class Main {

    private static void execute(LoadBalancingStrategy strategy, TaskGenerator generator) {
        generator.reset();
        StatsService.reset();

        while (generator.hasTasksLeft() || strategy.hasTasksLeft()) {
            generator.checkForNewTask(strategy);
            strategy.tick();
        }

        System.out.println(StatsService.getStats());
    }

    public static void main(String[] args) {
        TaskGenerator generator = TaskGenerator.fromRandom(Settings.NUM_CPUS, Settings.TASK_COUNT, Settings.MAX_ARRIVAL, Settings.MIN_EXEC, Settings.MAX_EXEC, Settings.MIN_UTIL, Settings.MAX_UTIL);

        System.out.println("Strategy1: ");
        LoadBalancingStrategy strategy1 = new Strategy1(Settings.NUM_CPUS);
        execute(strategy1, generator);

        System.out.println("Strategy2: ");
        LoadBalancingStrategy strategy2 = new Strategy2(Settings.NUM_CPUS);
        execute(strategy2, generator);

        System.out.println("Strategy3: ");
        LoadBalancingStrategy strategy3 = new Strategy3(Settings.NUM_CPUS);
        execute(strategy3, generator);
    }
}