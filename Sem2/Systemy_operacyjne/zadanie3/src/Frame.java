public class Frame {
    private int frameAgeTick;
    private int lastUsedTick;
    private int currentPage;

    public Frame() {
        this.clear();
    }

    public boolean isEmpty() {
        return this.currentPage < 0;
    }

    public void write(int page, int tick) {
        this.currentPage = page;
        this.frameAgeTick = tick;
        this.lastUsedTick = tick;
    }

    public void read(int tick) {
        this.lastUsedTick = tick;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getFrameAgeTick() {
        return frameAgeTick;
    }

    public int getLastUsedTick() {
        return lastUsedTick;
    }

    public void clear() {
        this.frameAgeTick = -1;
        this.lastUsedTick = -1;
        this.currentPage = -1;
    }

    @Override
    public String toString() {
        return "Frame{" + currentPage +
                '}';
    }
}
