public class Task {
    private final int arrivalTime;
    private final int initialExecTime;
    private int execTimeLeft;
    private final int cpuUtilization;

    public Task(int arrivalTime, int execTime, int cpuUtilization) {
        this.arrivalTime = arrivalTime;
        this.initialExecTime = execTime;
        this.execTimeLeft = execTime;
        this.cpuUtilization = cpuUtilization;
    }

    public void tick() {
        if (this.execTimeLeft > 0) {
            this.execTimeLeft--;
        }
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getInitialExecTime() {
        return initialExecTime;
    }

    public int getExecTimeLeft() {
        return execTimeLeft;
    }

    public int getCpuUtilization() {
        return cpuUtilization;
    }
}
