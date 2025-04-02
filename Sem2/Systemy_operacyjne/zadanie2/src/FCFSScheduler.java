import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

        if (this.headPosition == this.currentRequest.getSector()) {
            this.currentRequest.setStatus(DiscRequest.Status.FINISHED);
            this.currentRequest = null;

            //TODO stats
        }
    }

    private void checkForNewRequest() {
        if (this.currentRequest != null || this.requestQueue.isEmpty()) return;

        this.currentRequest = this.requestQueue.remove();
        this.headDirection = this.currentRequest.getSector() < this.headPosition ? LEFT : RIGHT;
        if (this.headPosition == this.currentRequest.getSector()) this.headDirection = STATIONARY;

        //TODO stats
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
