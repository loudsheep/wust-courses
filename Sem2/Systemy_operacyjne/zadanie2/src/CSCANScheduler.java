import java.util.Comparator;
import java.util.PriorityQueue;

public class CSCANScheduler extends DiscScheduler {
    private final PriorityQueue<DiscRequest> leftQueue, rightQueue;

    public CSCANScheduler(int sectorCount) {
        super(sectorCount, 0);
        this.headDirection = RIGHT;

        this.leftQueue = new PriorityQueue<>(Comparator.comparingInt(DiscRequest::getSector));
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
        if (!this.rightQueue.isEmpty()) {
            return this.rightQueue.remove();
        }

        return null;
    }

    @Override
    public void newRequest(DiscRequest request) {
        if (request.getSector() >= this.headPosition) {
            this.rightQueue.offer(request);
        } else {
            this.leftQueue.offer(request);
        }
    }

    @Override
    public boolean hasRequestsLeft() {
        return !this.leftQueue.isEmpty() || !this.rightQueue.isEmpty();
    }

    @Override
    protected void checkHeadOutOfBounds() {
        if (this.headPosition >= this.sectorCount) {
            this.headPosition = 0;
            this.headDirection = RIGHT;

            StatsService.headJumpsToBeginning();

            while(!this.leftQueue.isEmpty()) {
                this.rightQueue.offer(this.leftQueue.remove());
            }
        }
    }
}
