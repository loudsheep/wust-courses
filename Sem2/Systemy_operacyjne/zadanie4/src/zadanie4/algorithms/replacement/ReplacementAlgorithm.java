package zadanie4.algorithms.replacement;

import zadanie4.RequestGenerator;
import zadanie4.memory.Memory;
import zadanie4.MemoryRequest;

public abstract class ReplacementAlgorithm {
    protected int tick = -1;

    protected Memory memory;
    protected RequestGenerator requests;
    protected int currentRequestIdx;

    public ReplacementAlgorithm(Memory memory, RequestGenerator requests) {
        this.memory = memory;
        this.requests = requests;
        this.currentRequestIdx = 0;
    }

    public void tick() {
        this.tick++;
    }

    protected MemoryRequest nextRequest() {
        return this.requests.nextRequest();
    }

    public boolean hasRequestsLeft() {
        return this.requests.hasRequestsLeft();
    }

    public int getFramesAssigned() {
        return this.memory.getFrameCount();
    }

    public void changeFrameCountInMemory(int newSize) {
        this.memory.changeFrameCount(newSize);
    }

    public int getPagesUsed() {
        return this.requests.getPagesUsed();
    }

    public Memory getMemory() {
        return memory;
    }

    public void reset() {
        this.requests.reset();
    }
}
