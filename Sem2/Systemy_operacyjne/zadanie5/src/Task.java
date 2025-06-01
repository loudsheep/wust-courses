public class Task {
    private final int pid;
    private final int arrivalTime;
    private final int initialExecTime;
    private int execTimeLeft;
    private final int cpuUtilization;

    public Task(int pid, int arrivalTime, int execTime, int cpuUtilization) {
        this.pid = pid;
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

    public int getPid() {
        return pid;
    }

    public void reset() {
        this.execTimeLeft = this.initialExecTime;
    }
}
