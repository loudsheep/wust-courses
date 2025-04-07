public abstract class DiscScheduler {
    public static final int LEFT = -1;
    public static final int RIGHT = 1;
    public static final int STATIONARY = 0;

    protected int sectorCount;
    protected int headPosition;
    protected int headDirection = STATIONARY;
    protected int tick = 0;

    public DiscScheduler(int sectorCount, int headPosition) {
        this.sectorCount = sectorCount;
        this.headPosition = headPosition;
    }

    public void tick() {
        this.tick++;
        this.moveHead(this.headDirection);
    }

    public abstract void newRequest(DiscRequest request);

    public abstract void newRealTimeRequest(DiscRequest request);

    public abstract boolean hasRequestsLeft();

    protected void moveHead(int direction) {
        this.headPosition += direction;

        if (direction != STATIONARY) StatsService.headMoved();

        checkHeadOutOfBounds();
    }

    protected void checkHeadOutOfBounds() {
        if (this.headPosition >= this.sectorCount - 1) {
            this.headPosition = this.sectorCount - 1;
            this.headDirection = STATIONARY;
        } else if (this.headPosition <= 0) {
            this.headPosition = 0;
            this.headDirection = STATIONARY;
        }
    }

    protected void setHeadPosition(int position) {
        this.headPosition = position;
    }
}
