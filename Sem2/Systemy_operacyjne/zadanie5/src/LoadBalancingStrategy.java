import java.util.Random;

public abstract class LoadBalancingStrategy {
    private final Processor[] cpus;
    protected final Random random = new Random();

    public LoadBalancingStrategy(int cpuCount) {
        assert cpuCount > 0;

        this.cpus = new Processor[cpuCount];
        for (int i = 0; i < cpuCount; i++) {
            this.cpus[i] = new Processor(i + 1, this);
        }
    }

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
}
