package zadanie4.memory;

import java.util.LinkedList;
import java.util.Queue;

public class ALRUMemory extends Memory{
    private final Queue<Frame> fifo = new LinkedList<>();

    public ALRUMemory(int frameCount) {
        super(frameCount);

        fifo.addAll(this.frames);
    }

    public Frame getALRUFrame() {
        Frame f;
        while((f = this.fifo.poll()).pageHasReferenceBit()) {
            f.setPageReferenceBit(false);
            this.fifo.add(f);
        }

        return f;
    }

    @Override
    public void writeFrame(Frame frame, Page page, int tick) {
        super.writeFrame(frame, page, tick);

        frame.setPageReferenceBit(true);
        this.fifo.add(frame);
    }

    @Override
    public void readFrame(Frame frame, int tick) {
        super.readFrame(frame, tick);

        frame.setPageReferenceBit(true);
    }
}
