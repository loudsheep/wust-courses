public class DiscRequest {
    public enum Status {
        REGISTERED,
        FINISHED,
        STARVED
    }

    private int arrivalTime;
    private int sector;
    private boolean isRealTime;
    private Status status;

    public DiscRequest(int arrivalTime, int sector, boolean isRealTime) {
        this.arrivalTime = arrivalTime;
        this.sector = sector;
        this.isRealTime = isRealTime;
    }

    public DiscRequest(int arrivalTime, int sector) {
        this(arrivalTime, sector, false);
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getSector() {
        return sector;
    }

    public boolean isRealTime() {
        return isRealTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DiscRequest{" +
                "arv=" + arrivalTime +
                ", sec=" + sector +
                '}';
    }
}
