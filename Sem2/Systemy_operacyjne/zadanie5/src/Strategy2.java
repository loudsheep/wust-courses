public class Strategy2 extends LoadBalancingStrategy {
    public Strategy2(int cpuCount) {
        super(cpuCount);
    }

    @Override
    public boolean balancingCallback(Processor processor, Task task) {
        if (processor.getCurrentUtilization() <= Settings.P) {
            return false;
        }

        for (int i = 0; i < Settings.Z; i++) {
            Processor p = this.getRandomCpu(processor);

            StatsService.utilizationCheck();
            if (p.getCurrentUtilization() <= Settings.P) {
                StatsService.taskMigration();
                p.addTaskToQueue(task);
                return true;
            }
        }

        return false;
    }

    @Override
    public void tickCallback(Processor processor) {
    }
}
