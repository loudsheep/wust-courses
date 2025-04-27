public class FIFO extends PageReplacementAlgorithm {
    private int currentPageRequest;

    protected FIFO(int frameCount, int pageCount) {
        super(frameCount, pageCount);
        this.currentPageRequest = -1;
    }

    @Override
    protected void tick() {
        super.tick();

        if (this.currentPageRequest < 0) return;

        Frame oldest = null;
        for (Frame frame : this.frames) {
            if (oldest == null || frame.getFrameAgeTick() < oldest.getFrameAgeTick()) {
                oldest = frame;
            }

            if (frame.getCurrentPage() != this.currentPageRequest) continue;

            frame.read(this.tick);
            this.currentPageRequest = -1;
            return;
        }

        StatsService.pageFault();

        assert oldest != null;
        oldest.write(currentPageRequest, tick);

        this.currentPageRequest = -1;
    }

    @Override
    public void newRequest(int page) {
        this.currentPageRequest = page;
    }

    @Override
    public boolean hasRequestsLeft() {
        return false;
    }
}
