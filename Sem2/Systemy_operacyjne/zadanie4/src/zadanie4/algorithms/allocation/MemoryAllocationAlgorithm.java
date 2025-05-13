package zadanie4.algorithms.allocation;

import zadanie4.MemProcess;

import java.util.Arrays;

public abstract class MemoryAllocationAlgorithm {
    protected MemProcess[] processes;
    protected final int totalFrames;
    protected int freeFrames;

    public MemoryAllocationAlgorithm(int totalFrames, MemProcess[] processes) {
        this.processes = processes;
        this.totalFrames = totalFrames;
        this.freeFrames = 0;

        // initial allocation is equal
//        for (int i = 0; i < this.processes.length; i++) {
//            int div = totalFrames / (this.processes.length - i);
//
//            this.processes[i].changeFrameCountInMemory(div);
//            totalFrames -= div;
//        }
    }

    public void tick() {
        for (MemProcess process : this.processes) {
            if (process.hasRequestsLeft()) process.tick();
        }
        this.reassignFrames();
    }

    public boolean hasRequestsLeft() {
        for (MemProcess p : processes) {
            if (p.hasRequestsLeft()) return true;
        }
        return false;
    }

    protected abstract void reassignFrames();

    @Override
    public String toString() {
        return "MemoryAllocationAlgorithm{" +
                "processes=" + Arrays.toString(processes) +
                ", totalFrames=" + totalFrames +
                '}';
    }
}
