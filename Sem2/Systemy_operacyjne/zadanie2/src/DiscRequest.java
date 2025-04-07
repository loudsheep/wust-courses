public class DiscRequest {
    public enum Status {
        REGISTERED,
        FINISHED,
        STARVED
    }

    private int arrivalTime;
    private int sector;
    private int deadline;
    private Status status;

    public DiscRequest(int arrivalTime, int sector, int deadline) {
        this.arrivalTime = arrivalTime;
        this.sector = sector;
        this.deadline = deadline;
    }

    public DiscRequest(int arrivalTime, int sector) {
        this(arrivalTime, sector, -1);
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getSector() {
        return sector;
    }

    public boolean isRealTime() {
        return deadline >= 0;
    }

    public int getDeadline() {
        return deadline;
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
