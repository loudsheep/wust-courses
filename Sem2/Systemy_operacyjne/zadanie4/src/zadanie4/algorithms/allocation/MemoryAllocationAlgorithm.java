package zadanie4.algorithms.allocation;

import zadanie4.algorithms.replacement.ReplacementAlgorithm;

public abstract class MemoryAllocationAlgorithm {
    protected ReplacementAlgorithm[] replacementAlgorithms;

    public MemoryAllocationAlgorithm(ReplacementAlgorithm[] replacementAlgorithms, int totalFrames) {
        this.replacementAlgorithms = replacementAlgorithms;

        // initial allocation is equal
        for (int i = 0; i < this.replacementAlgorithms.length; i++) {
            int div = totalFrames / (this.replacementAlgorithms.length - i);

            this.replacementAlgorithms[i].getMemory().changeFrameCount(div);
            totalFrames -= div;
        }
    }

    public void tick() {
        for (ReplacementAlgorithm algorithm: this.replacementAlgorithms) {
            if (algorithm.hasRequestsLeft()) algorithm.tick();
        }
    }
}
