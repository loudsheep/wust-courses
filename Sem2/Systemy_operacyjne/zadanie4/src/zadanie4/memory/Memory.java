package zadanie4.memory;

import zadanie4.MemoryRequest;
import zadanie4.StatsService;

import java.util.*;

public class Memory {
    protected List<Frame> frames;
    protected int frameCount;
    protected final Random random = new Random();

    private final Queue<Boolean> recentRequests = new LinkedList<>();
    private final LinkedList<Page> recentPages = new LinkedList<>();
    private int faultsInPeriod = 0;

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
        trackPageFaultRatio(true);
        trackPageReference(page);
    }

    public void readFrame(Frame frame, int tick) {
        frame.read(tick);
        StatsService.pageRead();
        trackPageFaultRatio(false);
        trackPageReference(frame.getCurrentPage());
    }

    public void clear() {
        this.frames.clear();
        for (int i = 0; i < frameCount; i++) {
            this.frames.add(new Frame());
        }
        this.recentRequests.clear();
        this.recentPages.clear();
        this.faultsInPeriod = 0;
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

    private void trackPageFaultRatio(boolean isPageFault) {
        while (recentRequests.size() >= StatsService.PFF_DELTA_T) {
            boolean removed = recentRequests.poll();
            if (removed) {
                faultsInPeriod--;
            }
        }

        recentRequests.offer(isPageFault);
        if (isPageFault) {
            faultsInPeriod++;
        }
    }

    private void trackPageReference(Page page) {
        while (recentPages.size() >= StatsService.WSS_DELTA_T) {
            recentPages.removeFirst();
        }
        recentPages.addLast(page);
    }

    public float getPffRatio() {
        if (this.recentRequests.size() < StatsService.PFF_DELTA_T) return 0.5f;
        return (float) this.faultsInPeriod / StatsService.PFF_DELTA_T;
    }

    public int getWorkingSetSize(int deltaT) {
        if (deltaT <= 0) return 0;
        int maxSize = Math.min(deltaT, recentPages.size());

        Set<String> uniquePages = new HashSet<>();
        for (int i = recentPages.size() - maxSize; i < recentPages.size(); i++) {
            uniquePages.add(recentPages.get(i).getValue());
        }

        return uniquePages.size();
    }

    public void resetPffRatio() {
        this.faultsInPeriod = 0;
        this.recentRequests.clear();
    }

    @Override
    public String toString() {
        return "Memory{" + frames + '}';
    }
}
