import java.util.Comparator;
import java.util.PriorityQueue;

public class Processor {
    private int id;
    private int currentUtilization;
    LoadBalancingStrategy balancingStrategyRef;

    private final PriorityQueue<Task> tasks = new PriorityQueue<>(Comparator.comparingInt(Task::getExecTimeLeft));

    public Processor(int id, LoadBalancingStrategy ref) {
        this.id = id;
        this.currentUtilization = 0;
        this.balancingStrategyRef = ref;
    }

    public void tick() {
        // Tick every process
        for (Task t : this.tasks) {
            t.tick();
        }

        this.removeFinishedTasks();
    }

    private void removeFinishedTasks() {
        while (!this.tasks.isEmpty() && this.tasks.peek().getExecTimeLeft() <= 0) {
            // TODO: stats service
            Task t = this.tasks.remove();
            this.currentUtilization -= t.getCpuUtilization();
        }
    }

    public void addTaskToQueue(Task task) {
        // TODO: load balancing call
        this.tasks.offer(task);
        this.currentUtilization += task.getCpuUtilization();
    }

    public int getCurrentUtilization() {
        return currentUtilization;
    }
}
