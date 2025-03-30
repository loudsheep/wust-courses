import java.util.LinkedList;

public class RoundRobinHandler extends ProcessHandler {
    private CpuProcess currentProcess = null;
    private final LinkedList<CpuProcess> processQueue = new LinkedList<>();
    private final int slice;
    private int sliceTracker;

    public RoundRobinHandler(int slice) {
        this.slice = Math.max(slice, 1);
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
        this.sliceTracker--;

        if (this.currentProcess.getStatus() == CpuProcess.Status.FINISHED) {
            StatsService.fullExecution(this.currentProcess, this.tick);
            this.currentProcess = null;
            return;
        }

        if (this.sliceTracker <= 0) {
            this.registerNewProcess(this.currentProcess);
            this.currentProcess = null;
        }
    }

    private void checkForNewProcess() {
        if (this.currentProcess != null || this.processQueue.isEmpty()) return;

        this.currentProcess = this.processQueue.remove();

        StatsService.newProcessSwitch();
        if (this.currentProcess.getStatus() == CpuProcess.Status.REGISTERED) {
            StatsService.firstExecution(this.currentProcess, this.tick);
        }

        this.sliceTracker = this.slice;
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
