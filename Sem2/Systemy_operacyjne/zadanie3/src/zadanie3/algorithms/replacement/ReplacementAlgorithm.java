package zadanie3.algorithms.replacement;

import zadanie3.Memory;
import zadanie3.MemoryRequest;

public abstract class ReplacementAlgorithm {
    protected int tick = -1;

    protected Memory memory;
    protected MemoryRequest[] requests;
    protected int currentRequestIdx;

    protected ReplacementAlgorithm(Memory memory, MemoryRequest[] requests) {
        this.memory = memory;
        this.requests = requests;
        this.currentRequestIdx = 0;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public Memory getMemory() {
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
