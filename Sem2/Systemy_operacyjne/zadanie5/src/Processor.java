import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Processor {
    private int tick = -1;
    private final int pid;
    private int currentUtilization;
    LoadBalancingStrategy balancingStrategyRef;

    private final PriorityQueue<Task> executingTasks = new PriorityQueue<>(Comparator.comparingInt(Task::getExecTimeLeft));
    private final LinkedList<Task> waitingTasks = new LinkedList<>();

    public Processor(int pid, LoadBalancingStrategy ref) {
        this.pid = pid;
        this.currentUtilization = 0;
        this.balancingStrategyRef = ref;
    }

    public void tick() {
        this.tick++;

        this.checkWaitingTasks();

        StatsService.reportUtilization(this);
        if (this.executingTasks.isEmpty()) StatsService.emptyTick();

        // Tick every process
        for (Task t : this.executingTasks) {
            t.tick();
        }

        this.removeFinishedTasks();
    }

    private void checkWaitingTasks() {
        if (this.waitingTasks.isEmpty()) return;

        while(!this.waitingTasks.isEmpty() && this.currentUtilization + this.waitingTasks.peek().getCpuUtilization() <= Settings.MAX_CPU_UTIL) {
            Task t = this.waitingTasks.remove();

            StatsService.delayedTask(t, this.tick);
            this.executingTasks.offer(t);
        }
    }

    private void removeFinishedTasks() {
        while (!this.executingTasks.isEmpty() && this.executingTasks.peek().getExecTimeLeft() <= 0) {
            // TODO: stats service, task finished
            Task t = this.executingTasks.remove();
            this.currentUtilization -= t.getCpuUtilization();
        }
    }

    // When task appears this gets executed
    public void offerTask(Task task) {
        boolean taskTaken = this.balancingStrategyRef.balancingCallback(this, task);

        if (taskTaken) return;

        StatsService.taskNotMigrated();

        this.addTaskToQueue(task);
    }

    // Blindly accepts task, no balancing callback
    public void addTaskToQueue(Task task) {
        if (this.currentUtilization + task.getCpuUtilization() <= Settings.MAX_CPU_UTIL) {
            this.executingTasks.offer(task);
            this.currentUtilization += task.getCpuUtilization();
        } else {
            this.waitingTasks.add(task);
        }
    }

    public int getCurrentUtilization() {
        return currentUtilization;
    }

    public boolean hasTasksLeft() {
        return !this.executingTasks.isEmpty();
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
