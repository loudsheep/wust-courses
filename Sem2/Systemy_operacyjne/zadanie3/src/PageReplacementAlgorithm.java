
public abstract class PageReplacementAlgorithm {
    protected int tick = -1;
    protected final int FRAME_COUNT;
    protected final int PAGE_COUNT;

    protected Memory memory;
    protected Page[] requests;
    protected int currentRequestIdx;

    protected PageReplacementAlgorithm(int frameCount, int pageCount, Page[] requests) {
        FRAME_COUNT = frameCount;
        PAGE_COUNT = pageCount;

        this.memory = new Memory(frameCount);
        this.requests = requests;
        this.currentRequestIdx = 0;
    }

    public void tick() {
        this.tick++;
    }

    protected Page nextRequest() {
        return this.requests[this.currentRequestIdx++];
    }

    public boolean hasRequestsLeft() {
        return this.currentRequestIdx < this.requests.length;
    }
}
