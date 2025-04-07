import java.util.Comparator;
import java.util.PriorityQueue;

public class SCANWithEDFScheduler extends DiscScheduler {
    private final PriorityQueue<DiscRequest> leftQueue, rightQueue;

    public SCANWithEDFScheduler(int sectorCount) {
        super(sectorCount, 0);
        this.headDirection = RIGHT;

        this.leftQueue = new PriorityQueue<>(Comparator.comparingInt(DiscRequest::getSector).reversed());

        this.rightQueue = new PriorityQueue<>(Comparator.comparingInt(DiscRequest::getSector));
    }

    @Override
    public void tick() {
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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasRequestsLeft() {
        return !this.leftQueue.isEmpty() || !this.rightQueue.isEmpty();
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
