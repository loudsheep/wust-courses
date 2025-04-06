import jdk.jshell.spi.ExecutionControl;

import java.util.Comparator;
import java.util.TreeSet;

public class SSTFScheduler extends DiscScheduler {
    private final TreeSet<DiscRequest> requests;
    private DiscRequest currentRequest;

    public SSTFScheduler(int sectorCount, int headPosition) {
        super(sectorCount, headPosition);

        requests = new TreeSet<>(Comparator.comparingInt(DiscRequest::getSector));
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

            System.out.println("HANDLED: " + headPosition);

            StatsService.blockRead();
            StatsService.requestExecuted(this.currentRequest, this.tick);

            this.currentRequest = null;
            this.headDirection = STATIONARY;
        }
    }

    private void checkForNewRequest() {
        if (this.currentRequest != null || this.requests.isEmpty()) return;

        DiscRequest tmp = new DiscRequest(-1, this.headPosition);
        DiscRequest lower = this.requests.floor(tmp);
        DiscRequest higher = this.requests.ceiling(tmp);

        System.out.println(tmp.getSector() + " " + (lower == null ? "null" : lower.getSector()) + " " + (higher == null ? "null" : higher.getSector()));

        int distLower = lower != null ? Math.abs(lower.getSector() - headPosition) : Integer.MAX_VALUE;
        int distHigher = higher != null ? Math.abs(higher.getSector() - headPosition) : Integer.MAX_VALUE;

        DiscRequest closest;
        if (distLower <= distHigher) closest = lower;
        else closest = higher;

        // little redundant check, but why not
        if (closest == null) return;


        this.currentRequest = closest;
        this.requests.remove(closest);

        this.headDirection = this.currentRequest.getSector() < this.headPosition ? LEFT : RIGHT;
        if (this.headPosition == this.currentRequest.getSector()) this.headDirection = STATIONARY;
    }

    @Override
    public void newRequest(DiscRequest request) {
        this.requests.add(request);
    }

    @Override
    public void newRealTimeRequest(DiscRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean hasRequestsLeft() {
        return this.currentRequest != null || !this.requests.isEmpty();
    }
}
