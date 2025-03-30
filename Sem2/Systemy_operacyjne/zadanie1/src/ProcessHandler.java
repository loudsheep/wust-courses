public abstract class ProcessHandler {
    protected int tick = 0;

    public void tick() {
        this.tick++;
    }

    public abstract void registerNewProcess(CpuProcess process);

    public abstract boolean hasProcessesLeft();
}
