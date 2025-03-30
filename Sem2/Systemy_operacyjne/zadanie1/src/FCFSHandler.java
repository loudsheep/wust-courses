import java.util.LinkedList;

public class FCFSHandler extends ProcessHandler {
    private CpuProcess currentProcess = null;
    private final LinkedList<CpuProcess> processQueue = new LinkedList<>();

    public FCFSHandler() {
    }

    @Override
    public void tick() {
        this.checkForNewProcess();

        super.tick();

        if (currentProcess == null) {
            StatsService.emptyTick();
            return;
        }

        this.currentProcess.tick();

        if (this.currentProcess.getStatus() == CpuProcess.Status.FINISHED) {
            StatsService.fullExecution(this.currentProcess, this.tick);
            this.currentProcess = null;
        }
    }

    private void checkForNewProcess() {
        if (this.currentProcess != null || this.processQueue.isEmpty()) return;

        this.currentProcess = this.processQueue.remove();

        StatsService.firstExecution(this.currentProcess, this.tick);
        StatsService.newProcessSwitch();
    }

    @Override
    public void registerNewProcess(CpuProcess process) {
        this.processQueue.add(process);
    }

    @Override
    public boolean hasProcessesLeft() {
        return this.currentProcess != null || !this.processQueue.isEmpty();
    }
}
