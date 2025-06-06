package zadanie3.memory;

import zadanie3.MemoryRequest;
import zadanie3.StatsService;

import java.util.*;

public class Memory {
    protected List<Frame> frames;
    protected int frameCount;
    protected final Random random = new Random();

    public Memory(int frameCount) {
        assert frameCount > 0;

        this.frameCount = frameCount;
        this.frames = new LinkedList<>();
        for (int i = 0; i < frameCount; i++) {
            this.frames.add(new Frame());
        }
    }

    private Frame searchForPage(Page page, List<Frame> list) {
        for (Frame frame : list) {
            if (page.equals(frame.getCurrentPage())) return frame;
        }
        return null;
    }

    public Frame searchForPage(Page page) {
        return this.searchForPage(page, this.frames);
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

    public Frame getLRUFrame() {
        Frame tmp = this.frames.getFirst();

        for (Frame frame : this.frames) {
            if (frame.getFrameReadTick() < tmp.getFrameReadTick()) {
                tmp = frame;
            }
        }

        return tmp;
    }

    public Frame getRandomFrame() {
        return this.frames.get(random.nextInt(0, this.frameCount));
    }

    public Frame getOptimalFrame(MemoryRequest[] requests, int startIdx) {
        if (this.frameCount == 1) return this.frames.getFirst();

        List<Frame> tmp = new LinkedList<>(this.frames);

        for (int i = startIdx; i < requests.length && tmp.size() > 1; i++) {
            Frame search = this.searchForPage(requests[i].getPage(), tmp);
            if (search == null) continue;

            tmp.remove(search);
        }

        return tmp.getFirst();
    }

    public void writeFrame(Frame frame, Page page, int tick) {
        frame.write(page, tick);
        StatsService.pageFault();
    }

    public void readFrame(Frame frame, int tick) {
        frame.read(tick);
        StatsService.pageRead();
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

    public Frame getFirstFrame() {
        return this.frames.getFirst();
    }

    @Override
    public String toString() {
        return "Memory{" + frames + '}';
    }
}
