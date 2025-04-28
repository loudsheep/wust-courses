import java.util.Arrays;

public class Memory {
    private Frame[] frames;
    private int frameCount;

    public Memory(int frameCount) {
        assert frameCount >= 0;

        this.frameCount = frameCount;
        this.frames = new Frame[frameCount];
        for (int i = 0; i < frameCount; i++) {
            this.frames[i] = new Frame();
        }
    }

    public Frame searchForPage(Page page) {
        for (Frame frame : this.frames) {
            if (page.equals(frame.getCurrentPage())) return frame;
        }
        return null;
    }

    public Frame getOldestFrame() {
        Frame tmp = this.frames[0];
        for (Frame frame : this.frames) {
            if (frame.getFrameWriteTick() < tmp.getFrameWriteTick()) {
                tmp = frame;
            }
        }
        return tmp;
    }

    public Frame getLeastRecentlyUsedFrame() {
        Frame tmp = this.frames[0];
        for (Frame frame : this.frames) {
            if (frame.getFrameReadTick() < tmp.getFrameReadTick()) {
                tmp = frame;
            }
        }
        return tmp;
    }

    public void clear() {
        for (int i = 0; i < this.frames.length; i++) {
            this.frames[i] = new Frame();
        }
    }

    public int getFrameCount() {
        return this.frameCount;
    }

    public void changeFrameCount(int newSize) {
        assert newSize >= 0;
        if (newSize > this.frameCount) {
            Frame[] tmp = new Frame[newSize];
            System.arraycopy(this.frames, 0, tmp, 0, this.frameCount);
            this.frameCount = newSize;
            this.frames = tmp;
            return;
        }

        Frame[] tmp = new Frame[newSize];
        System.arraycopy(this.frames, 0, tmp, 0, newSize);
        this.frameCount = newSize;
        this.frames = tmp;
    }

    @Override
    public String toString() {
        return "Memory{" + Arrays.toString(frames) + '}';
    }
}
