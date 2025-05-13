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

    public void tick() {
        this.tick++;
    }

    protected MemoryRequest nextRequest() {
        return this.requests[this.currentRequestIdx++];
    }

    public boolean hasRequestsLeft() {
        return this.currentRequestIdx < this.requests.length;
    }

    public int getFramesAssigned() {
        return this.memory.getFrameCount();
    }

    public void changeFrameCountInMemory(int newSize) {
        this.memory.changeFrameCount(newSize);
    }

    public int getPagesUsed() {
        return pagesUsed;
    }

    public Memory getMemory() {
        return memory;
    }

    public void reset() {
        this.currentRequestIdx = 0;
    }
}
