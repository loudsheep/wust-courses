package zadanie3;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Memory {
    private List<Frame> frames;
    private int frameCount;

    public Memory(int frameCount) {
        assert frameCount >= 0;

        this.frameCount = frameCount;
        this.frames = new LinkedList<>();
        for (int i = 0; i < frameCount; i++) {
            this.frames.add(new Frame());
        }
    }

    public Frame searchForPage(Page page) {
        for (Frame frame : this.frames) {
            if (page.equals(frame.getCurrentPage())) return frame;
        }
        return null;
    }

    public Frame getOldestFrame() {
        Frame tmp = this.frames.getFirst();

        for (Frame frame : this.frames) {
            if (frame.getFrameWriteTick() < tmp.getFrameWriteTick()) {
                tmp = frame;
            }
        }

        return tmp;
    }

    public Frame getLeastRecentlyUsedFrame() {
        Frame tmp = this.frames.getFirst();

        for (Frame frame : this.frames) {
            if (frame.getFrameReadTick() < tmp.getFrameReadTick()) {
                tmp = frame;
            }
        }

        return tmp;
    }

    public void clear() {
        this.frames.clear();
        for (int i = 0; i < frameCount; i++) {
            this.frames.add(new Frame());
        }
    }

    public int getFrameCount() {
        return this.frameCount;
    }

    public void changeFrameCount(int newSize) {
        assert newSize >= 0;

        if (this.frameCount < newSize) {
            while (this.frameCount < newSize) {
                this.frames.add(new Frame());
                this.frameCount++;
            }
            return;
        }

        while (this.frameCount > newSize) {
            this.frames.removeLast();
            this.frameCount--;
        }
    }

    @Override
    public String toString() {
        return "Memory{" + frames + '}';
    }
}
