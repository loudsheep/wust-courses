public class Frame {
    private int frameWriteTick;
    private int frameReadTick;
    private Page currentPage;

    public Frame() {
        this.clear();
    }

    public boolean isEmpty() {
        return this.currentPage == null;
    }

    public void write(Page page, int tick) {
        this.currentPage = page;
        this.frameWriteTick = tick;
        this.frameReadTick = tick;
    }

    public void read(int tick) {
        this.frameReadTick = tick;
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    public int getFrameWriteTick() {
        return frameWriteTick;
    }

    public int getFrameReadTick() {
        return frameReadTick;
    }

    public void clear() {
        this.frameWriteTick = -1;
        this.frameReadTick = -1;
        this.currentPage = null;
    }

    @Override
    public String toString() {
        return "Frame{" + currentPage +
                '}';
    }
}
