public class Strategy1 extends LoadBalancingStrategy {
    public Strategy1(int cpuCount) {
        super(cpuCount);
    }

    @Override
    public boolean balancingCallback(Processor processor, Task task) {
        for (int i = 0; i < Settings.Z; i++) {
            Processor p = this.getRandomCpu(processor);

            if (p.getCurrentUtilization() < Settings.P) {
                p.addTaskToQueue(task);
                return true;
            }
        }

        return false;
    }
}
