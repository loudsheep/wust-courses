import java.util.Arrays;

public abstract class PageReplacementAlgorithm {
    protected int tick = -1;
    protected final int FRAME_COUNT;
    protected final int PAGE_COUNT;

    protected Frame[] frames;

    protected PageReplacementAlgorithm(int frameCount, int pageCount) {
        FRAME_COUNT = frameCount;
        PAGE_COUNT = pageCount;

        this.frames = new Frame[FRAME_COUNT];
        for (int i = 0; i < FRAME_COUNT; i++) {
            this.frames[i] = new Frame();
        }
    }

    protected void tick() {
        this.tick++;
        System.out.println(Arrays.toString(this.frames));
    }

    public abstract void newRequest(int page);

    public abstract boolean hasRequestsLeft();
}
