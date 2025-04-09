import java.util.LinkedList;

public class FCFSScheduler extends DiscScheduler {
    private final LinkedList<DiscRequest> requestQueue = new LinkedList<>();
    private DiscRequest currentRequest;

    public FCFSScheduler(int sectorCount, int headPosition) {
        super(sectorCount, headPosition);
    }

    @Override
    public void tick() {
        this.checkForNewRequest();

        super.tick();

        if (this.currentRequest == null) {
            StatsService.emptyTick();
            return;
        }

        if (this.headPosition == this.currentRequest.getSector()) {
            this.currentRequest.setStatus(DiscRequest.Status.FINISHED);

            StatsService.blockRead();
            StatsService.requestExecuted(this.currentRequest, this.tick);

            this.currentRequest = null;
            this.headDirection = STATIONARY;
        }
    }

    private void checkForNewRequest() {
        if (this.currentRequest != null || this.requestQueue.isEmpty()) return;

        this.currentRequest = this.requestQueue.remove();
        this.headDirection = this.currentRequest.getSector() < this.headPosition ? LEFT : RIGHT;
        if (this.headPosition == this.currentRequest.getSector()) this.headDirection = STATIONARY;

    }

    @Override
    public void newRequest(DiscRequest request) {
        this.requestQueue.add(request);
    }

    @Override
    public boolean hasRequestsLeft() {
        return this.currentRequest != null || !this.requestQueue.isEmpty();
    }
}
