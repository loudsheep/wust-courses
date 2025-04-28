package zadanie3;

import zadanie3.algorithms.replacement.ReplacementAlgorithm;

public class Process {
    private final int pid;
    private final ReplacementAlgorithm replacementAlgorithm;

    public Process(int pid, ReplacementAlgorithm replacementAlgorithm) {
        this.pid = pid;
        this.replacementAlgorithm = replacementAlgorithm;
    }

    public void tick() {
        this.replacementAlgorithm.tick();
    }

    public boolean hasRequestsLeft() {
        return this.replacementAlgorithm.hasRequestsLeft();
    }

    public int getPid() {
        return pid;
    }

    public Memory getMemory() {
        return this.replacementAlgorithm.getMemory();
    }
}
