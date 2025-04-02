public abstract class DiscScheduler {
    public static final int LEFT = -1;
    public static final int RIGHT = 1;
    public static final int STATIONARY = 0;

    protected int sectorCount;
    protected int headPosition;
    protected int headDirection = 0;
    protected int tick;

    public DiscScheduler(int sectorCount, int headPosition) {
        this.sectorCount = sectorCount;
        this.headPosition = headPosition;
        this.tick = 0;
    }

    public void tick() {
        this.tick++;
        this.moveHead(this.headDirection);
    }

    public abstract void newRequest(DiscRequest request);

    public abstract boolean hasRequestsLeft();

    public void moveHead(int direction) {
        this.headPosition += direction;

        if (this.headPosition >= this.sectorCount) {
            this.headPosition = this.sectorCount - 1;
            this.headDirection = STATIONARY;
        } else if (this.headPosition < 0) {
            this.headPosition = 0;
            this.headDirection = STATIONARY;
        }
    }

    public void setHeadPosition(int position) {
        this.headPosition = position;
    }
}
