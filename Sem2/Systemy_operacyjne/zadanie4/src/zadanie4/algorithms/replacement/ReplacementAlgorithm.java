package zadanie4.algorithms.replacement;

import zadanie4.memory.Memory;
import zadanie4.MemoryRequest;

public abstract class ReplacementAlgorithm {
    protected int tick = -1;

    protected final int pagesUsed;

    protected Memory memory;
    protected MemoryRequest[] requests;
    protected int currentRequestIdx;

    public ReplacementAlgorithm(Memory memory, MemoryRequest[] requests, int pagesUsed) {
        this.memory = memory;
        this.requests = requests;
        this.currentRequestIdx = 0;
        this.pagesUsed = pagesUsed;
    }

    public final void setMemory(Memory memory) {
        this.memory = memory;
    }

    public final Memory getMemory() {
        return memory;
    }

    public void tick() {
        this.tick++;
    }

    protected MemoryRequest nextRequest() {
        return this.requests[this.currentRequestIdx++];
    }

    public boolean hasRequestsLeft() {
        return this.currentRequestIdx < this.requests.length;
    }

}
