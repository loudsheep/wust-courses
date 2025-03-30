import java.util.PriorityQueue;
import java.util.Comparator;

public class SJFHandler extends ProcessHandler {
    private CpuProcess currentProcess = null;
    private final PriorityQueue<CpuProcess> processQueue;
//    private boolean preemptive;

    public SJFHandler() {
        this.processQueue = new PriorityQueue<>(Comparator.comparingInt(CpuProcess::getRemainingTime));
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

//    private void checkForNewProcess() {
////        if (this.processQueue.isEmpty())
//        if ((this.currentProcess != null && !this.preemptive) || this.processQueue.isEmpty()) return;
//
//        if (!preemptive || this.currentProcess == null) {
//            currentProcess = processQueue.remove();
//            StatsService.firstExecution(currentProcess, this.tick);
//            StatsService.newProcessSwitch();
//
//            return;
//        }
//
//        CpuProcess nextProcess = processQueue.peek();
//        if (nextProcess.getExecutionTime() < currentProcess.getRemainingTime()) {
//            processQueue.offer(currentProcess);
//            currentProcess = processQueue.poll();
//            StatsService.firstExecution(currentProcess, this.tick);
//            StatsService.newProcessSwitch();
//        }
//
////        if (currentProcess == null && !processQueue.isEmpty()) {
////
////        } else if (preemptive && !processQueue.isEmpty()) {
////            // Preemptive SJF: Replace the current process if a shorter one arrives
////
////        }
//    }

    @Override
    public void registerNewProcess(CpuProcess process) {
        this.processQueue.offer(process);
    }

    @Override
    public boolean hasProcessesLeft() {
        return this.currentProcess != null || !this.processQueue.isEmpty();
    }
}
