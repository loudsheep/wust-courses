import java.util.Comparator;
import java.util.PriorityQueue;

public class Processor {
    private int tick = -1;
    private final int pid;
    private int currentUtilization;
    LoadBalancingStrategy balancingStrategyRef;

    private final PriorityQueue<Task> tasks = new PriorityQueue<>(Comparator.comparingInt(Task::getExecTimeLeft));

    public Processor(int pid, LoadBalancingStrategy ref) {
        this.pid = pid;
        this.currentUtilization = 0;
        this.balancingStrategyRef = ref;
    }

    public void tick() {
        this.tick++;

        StatsService.reportUtilization(this);
        if (this.tasks.isEmpty()) StatsService.emptyTick();

        // Tick every process
        for (Task t : this.tasks) {
            t.tick();
        }

        this.removeFinishedTasks();
    }

    private void removeFinishedTasks() {
        while (!this.tasks.isEmpty() && this.tasks.peek().getExecTimeLeft() <= 0) {
            // TODO: stats service, task finished
            Task t = this.tasks.remove();
            this.currentUtilization -= t.getCpuUtilization();
        }
    }

    // When task appears this gets executed
    public void offerTask(Task task) {
        boolean taskTaken = this.balancingStrategyRef.balancingCallback(this, task);

        if (taskTaken) return;

        this.addTaskToQueue(task);
    }

    // Blindly accepts task, no balancing callback
    public void addTaskToQueue(Task task) {
        // TODO: halt task if not enough cpu power
        this.tasks.offer(task);
        this.currentUtilization += task.getCpuUtilization();

        assert this.currentUtilization <= Settings.MAX_CPU_UTIL;
    }

    public int getCurrentUtilization() {
        return currentUtilization;
    }

    public boolean hasTasksLeft() {
        return !this.tasks.isEmpty();
    }

    public int getPid() {
        return pid;
    }

    @Override
    public String toString() {
        return "Processor{" +
                "pid=" + pid +
                ", currentUtilization=" + currentUtilization +
                '}';
    }
}
