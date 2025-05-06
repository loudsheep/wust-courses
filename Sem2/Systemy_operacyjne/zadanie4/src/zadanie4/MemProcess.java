package zadanie4;

import zadanie4.memory.Memory;
import zadanie4.algorithms.replacement.ReplacementAlgorithm;

public class MemProcess {
    private final int pid;
    private final ReplacementAlgorithm replacementAlgorithm;
    private boolean paused;

    public MemProcess(int pid, ReplacementAlgorithm replacementAlgorithm) {
        this.pid = pid;
        this.replacementAlgorithm = replacementAlgorithm;

        this.paused = this.replacementAlgorithm.getFramesAssigned() == 0;
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
        this.replacementAlgorithm.changeFrameCountInMemory(newSize);

        this.paused = newSize == 0;
    }

    public int getFramesAssigned() {
        return this.replacementAlgorithm.getFramesAssigned();
    }

    public int getPagesUsed() {
        return this.replacementAlgorithm.getPagesUsed();
    }

    @Override
    public String toString() {
        return "MemProcess{" +
                "frames=" + this.getFramesAssigned() +
                '}';
    }
}
