import java.util.Comparator;
import java.util.PriorityQueue;

public class SCANWithEDFScheduler extends DiscScheduler {
    private final PriorityQueue<DiscRequest> leftQueue, rightQueue;

    private final PriorityQueue<DiscRequest> realtimeRequests;
    private DiscRequest currentRTRequest;

    public SCANWithEDFScheduler(int sectorCount) {
        super(sectorCount, 0);
        this.headDirection = RIGHT;

        this.leftQueue = new PriorityQueue<>(Comparator.comparingInt(DiscRequest::getSector).reversed());
        this.rightQueue = new PriorityQueue<>(Comparator.comparingInt(DiscRequest::getSector));

        this.realtimeRequests = new PriorityQueue<>(Comparator.comparingInt(DiscRequest::getDeadline));
    }

    @Override
    public void tick() {
        this.checkForRTRequests();

        if (this.currentRTRequest != null && this.headPosition == this.currentRTRequest.getSector()) {
            this.currentRTRequest.checkRealtimeExecution(this.tick);

            StatsService.blockRead();
            StatsService.RTrequestExecuted(this.currentRTRequest, this.tick);

            this.currentRTRequest = null;
        }

        if (this.currentRTRequest != null) {
            this.rearrangeQueues();

            super.tick();
            return;
        }

        DiscRequest current = getRequestToProcess();

        if (current == null) StatsService.emptyTick();

        while (current != null) {
            current.setStatus(DiscRequest.Status.FINISHED);

            StatsService.blockRead();
            StatsService.requestExecuted(current, this.tick);

            current = getRequestToProcess();
        }

        super.tick();
    }

    private DiscRequest getRequestToProcess() {
        if (!this.leftQueue.isEmpty() && this.leftQueue.peek().getSector() == this.headPosition) {
            return this.leftQueue.remove();
        }

        if (!this.rightQueue.isEmpty() && this.rightQueue.peek().getSector() == this.headPosition) {
            return this.rightQueue.remove();
        }

        return null;
    }

    private void checkForRTRequests() {
        if (this.currentRTRequest != null || this.realtimeRequests.isEmpty()) return;

        this.currentRTRequest = this.realtimeRequests.remove();

        if (this.headPosition != this.currentRTRequest.getSector()) {
            this.headDirection = this.currentRTRequest.getSector() < this.headPosition ? LEFT : RIGHT;
        }
    }

    private void rearrangeQueues() {
        while(!this.leftQueue.isEmpty() && this.leftQueue.peek().getSector() > this.headPosition) {
            this.rightQueue.offer(this.leftQueue.remove());
        }

        while(!this.rightQueue.isEmpty() && this.rightQueue.peek().getSector() < this.headPosition) {
            this.leftQueue.offer(this.rightQueue.remove());
        }
    }

    @Override
    public void newRequest(DiscRequest request) {
        if (request.getSector() <= this.headPosition) {
            this.leftQueue.offer(request);
        } else {
            this.rightQueue.offer(request);
        }
    }

    @Override
    public void newRealTimeRequest(DiscRequest request) {
        this.realtimeRequests.offer(request);
    }

    @Override
    public boolean hasRequestsLeft() {
        return !this.leftQueue.isEmpty() || !this.rightQueue.isEmpty() || !this.realtimeRequests.isEmpty() || this.currentRTRequest != null;
    }

    @Override
    protected void checkHeadOutOfBounds() {
        if (this.headPosition >= this.sectorCount - 1) {
            this.headPosition = this.sectorCount - 1;
            this.headDirection = LEFT;
        } else if (this.headPosition <= 0) {
            this.headPosition = 0;
            this.headDirection = RIGHT;
        }
    }
}
