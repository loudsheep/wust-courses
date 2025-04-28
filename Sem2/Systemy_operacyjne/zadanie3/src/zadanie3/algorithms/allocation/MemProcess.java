package zadanie3.algorithms.allocation;

import zadanie3.Memory;
import zadanie3.algorithms.replacement.ReplacementAlgorithm;

public class MemProcess {
    private final int pid;
    private final ReplacementAlgorithm replacementAlgorithm;

    public MemProcess(int pid, ReplacementAlgorithm replacementAlgorithm) {
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
