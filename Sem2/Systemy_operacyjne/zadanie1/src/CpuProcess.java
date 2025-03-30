public class CpuProcess {
    enum Status {
        REGISTERED,
        PARTIALLY_EXECUTED,
        FINISHED,
    }

    private int executionTime;
    private int remainingTime;
    private int registeredAt;
    private Status status;

    public CpuProcess(int executionTime, int registeredAt) {
        this.executionTime = executionTime;
        this.remainingTime = executionTime;
        this.registeredAt = registeredAt;
        this.status = Status.REGISTERED;
    }

    public void tick() {
        this.remainingTime--;
        if (this.remainingTime <= 0) {
            this.status = Status.FINISHED;
        } else {
            this.status = Status.PARTIALLY_EXECUTED;
        }
    }

    public int getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(int registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CpuProcess{" +
                "executionTime=" + executionTime +
                ", remainingTime=" + remainingTime +
                ", registeredAt=" + registeredAt +
                '}';
    }
}
