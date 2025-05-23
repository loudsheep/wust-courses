package zadanie4.algorithms.allocation;

import zadanie4.MemProcess;
import zadanie4.StatsService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class MemoryAllocationAlgorithm {
    protected List<MemProcess> processes;
    protected final int totalFrames;
    protected int freeFrames;
    protected int tick = -1;

    public MemoryAllocationAlgorithm(int totalFrames, MemProcess[] processes) {
        this.processes = new LinkedList<>(Arrays.asList(processes));
        this.totalFrames = totalFrames;
        this.freeFrames = 0;
    }

    public void tick() {
        this.tick++;

        List<MemProcess> memProcesses = this.processes;
        for (int i = memProcesses.size() - 1; i >= 0; i--) {
            MemProcess process = memProcesses.get(i);
            if (process.hasRequestsLeft()) process.tick();
            else {
                processes.remove(process);
                this.freeFrames += process.getFramesAssigned();
                this.onProcessRemove();
                StatsService.processTickResult(process.getPid(), process.getReplacementAlgorithm().getTick());
            }
        }
    }

    public boolean hasRequestsLeft() {
        for (MemProcess p : processes) {
            if (p.hasRequestsLeft()) return true;
        }
        return false;
    }

    protected final void onFrameAllocChange() {
        if (StatsService.DEBUG_PRINT) {
            System.out.println(this);
        }
    }

    protected abstract void reassignFrames();

    protected abstract void onProcessRemove();

    @Override
    public String toString() {
        return "MemoryAllocationAlgorithm{" +
                "processes=" + processes +
                ", totalFrames=" + totalFrames +
                '}';
    }
}
