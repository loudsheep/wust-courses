import java.util.Arrays;
import java.util.Random;

public abstract class LoadBalancingStrategy {
    private int tick = -1;
    private final Processor[] cpus;
    protected final Random random = new Random();

    public LoadBalancingStrategy(int cpuCount) {
        assert cpuCount > 0;

        this.cpus = new Processor[cpuCount];
        for (int i = 0; i < cpuCount; i++) {
            this.cpus[i] = new Processor(i, this);
        }
    }

    public void tick() {
        this.tick++;

        for (Processor p : this.cpus) {
            p.tick();
        }
    }

    public int getCurrentTick() {
        return tick;
    }

    public final void newTask(Task task) {
        int pid = task.getPid();

        assert pid >= 0 && pid < this.cpus.length;

        this.cpus[pid].offerTask(task);
    }

    public boolean hasTasksLeft() {
        for (Processor cpu : this.cpus) {
            if (cpu.hasTasksLeft()) return true;
        }
        return false;
    }

    public abstract boolean balancingCallback(Processor processor, Task task);

    public abstract void tickCallback(Processor processor);

    protected final Processor getRandomCpu() {
        return this.cpus[this.random.nextInt(0, this.cpus.length)];
    }

    protected final Processor getRandomCpu(Processor exclude) {
        assert this.cpus.length > 1;

        Processor res;
        do {
            res = this.cpus[this.random.nextInt(0, this.cpus.length)];
        } while (res == exclude);

        return res;
    }

    @Override
    public String toString() {
        return "LoadBalancingStrategy{" +
                "cpus=" + Arrays.toString(cpus) +
                '}';
    }
}
