
import java.util.*;

public class SSTFScheduler extends DiscScheduler {
    //    private final TreeMap<Integer, DiscRequest> requests;
    private final TreeMap<Integer, List<DiscRequest>> requests;
    private DiscRequest currentRequest;

    public SSTFScheduler(int sectorCount, int headPosition) {
        super(sectorCount, headPosition);

        requests = new TreeMap<>();
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
        if (this.currentRequest != null || this.requests.isEmpty()) return;

        Map.Entry<Integer, List<DiscRequest>> lowerEntry = this.requests.floorEntry(this.headPosition);
        Map.Entry<Integer, List<DiscRequest>> higherEntry = this.requests.ceilingEntry(this.headPosition);

        List<DiscRequest> closest = getClosestRequest(lowerEntry, higherEntry);

        if (closest == null) return;

        this.currentRequest = closest.removeFirst();

        if (closest.isEmpty()) this.requests.remove(this.currentRequest.getSector());

        this.headDirection = this.currentRequest.getSector() < this.headPosition ? LEFT : RIGHT;
        if (this.headPosition == this.currentRequest.getSector()) this.headDirection = STATIONARY;
    }

    private List<DiscRequest> getClosestRequest(Map.Entry<Integer, List<DiscRequest>> lowerEntry, Map.Entry<Integer, List<DiscRequest>> higherEntry) {
        List<DiscRequest> lower = lowerEntry == null ? null : lowerEntry.getValue();
        List<DiscRequest> higher = higherEntry == null ? null : higherEntry.getValue();

        int distLower = lowerEntry != null ? Math.abs(lowerEntry.getKey() - headPosition) : Integer.MAX_VALUE;
        int distHigher = higherEntry != null ? Math.abs(higherEntry.getKey() - headPosition) : Integer.MAX_VALUE;

        List<DiscRequest> closest;
        if (distLower < distHigher) closest = lower;
        else closest = higher;
        return closest;
    }

    @Override
    public void newRequest(DiscRequest request) {
        this.requests.computeIfAbsent(request.getSector(), k -> new LinkedList<>()).add(request);
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
