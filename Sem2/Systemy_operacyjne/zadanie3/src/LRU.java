public class LRU extends PageReplacementAlgorithm {

    protected LRU(int frameCount, int pageCount, Page[] requests) {
        super(frameCount, pageCount, requests);
    }

    @Override
    public void tick() {
        super.tick();

        Page currentPage = this.nextRequest();

        Frame search = this.memory.searchForPage(currentPage);

        if (search != null) {
            search.read(tick);
            return;
        }

        StatsService.pageFault();

        Frame oldest = this.memory.getLeastRecentlyUsedFrame();
        oldest.write(currentPage, tick);
    }
}
