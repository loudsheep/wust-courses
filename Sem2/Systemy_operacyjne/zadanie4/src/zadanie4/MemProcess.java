package zadanie4;

import zadanie4.memory.Memory;
import zadanie4.algorithms.replacement.ReplacementAlgorithm;

public class MemProcess {
    private final int pid;
    private final ReplacementAlgorithm replacementAlgorithm;
    private boolean paused = false;

    public MemProcess(int pid, ReplacementAlgorithm replacementAlgorithm) {
        this.pid = pid;
        this.replacementAlgorithm = replacementAlgorithm;

        this.paused = this.replacementAlgorithm.getMemory().getFrameCount() == 0;
    }

    public void tick() {
        if (this.paused) return;

        this.replacementAlgorithm.tick();
    }

    public boolean hasRequestsLeft() {
        return this.replacementAlgorithm.hasRequestsLeft();
    }

    public int getPid() {
        return pid;
    }

    public void changeFrameCountInMemory(int newSize) {
        this.replacementAlgorithm.getMemory().changeFrameCount(newSize);

        this.paused = newSize == 0;
    }
}
