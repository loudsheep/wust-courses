public class Strategy3 extends Strategy2 {
    public Strategy3(int cpuCount) {
        super(cpuCount);
    }

    @Override
    public void tickCallback(Processor processor) {
        if (processor.getCurrentUtilization() >= Settings.R) return;

        Processor randomCpu = this.getRandomCpu(processor);
        StatsService.utilizationCheck();
        if (randomCpu.getCurrentUtilization() <= Settings.P) return;

        StatsService.taskTakenAway();
        processor.addTaskToQueue(randomCpu.takeTask());
    }
}
