package zadanie4.memory;

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

    protected void write(Page page, int tick) {
        this.currentPage = page;
        this.frameWriteTick = tick;
        this.frameReadTick = tick;
    }

    protected void read(int tick) {
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

    public boolean pageHasReferenceBit() {
        return this.currentPage != null && this.currentPage.hasReferenceBitSet();
    }

    public void setPageReferenceBit(boolean value) {
        if (this.currentPage == null) return;
        this.currentPage.setReferenceBit(value);
    }

    public void clear() {
        this.frameWriteTick = -1;
        this.frameReadTick = -1;
        this.currentPage = null;
    }

    @Override
    public String toString() {
        return "Frame{" + currentPage + '}';
    }
}
