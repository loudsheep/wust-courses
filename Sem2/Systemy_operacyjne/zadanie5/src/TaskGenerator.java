import java.util.*;

public class TaskGenerator {
    private final List<Task> tasks;
    private int taskIdx = 0;

    private TaskGenerator(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void checkForNewTask(LoadBalancingStrategy strategy) {
        if (!this.hasTasksLeft()) return;

        while (this.hasTasksLeft() && this.tasks.get(this.taskIdx).getArrivalTime() <= strategy.getCurrentTick()) {
            Task t = this.tasks.get(taskIdx);
            t.reset();
            strategy.newTask(t);
            this.taskIdx++;
        }
    }

    public boolean hasTasksLeft() {
        return this.taskIdx < this.tasks.size();
    }

    public void reset() {
        this.taskIdx = 0;
    }

    public static TaskGenerator fromRandom(int cpuCount, int taskCount, int maxArrivalTime, int minExecTime, int maxExecTime, int minCpuUtil, int maxCpuUtil) {
        PriorityQueue<Task> tasks = new PriorityQueue<>(Comparator.comparingInt(Task::getArrivalTime));
        Random random = new Random();

        for (int i = 0; i < taskCount; i++) {
            int pid = random.nextInt(cpuCount);
            int arrivalTime = random.nextInt(maxArrivalTime);
            int execTime = random.nextInt(minExecTime, maxExecTime);
            int util = random.nextInt(minCpuUtil, maxCpuUtil);

            Task t = new Task(pid, arrivalTime, execTime, util);
            tasks.offer(t);
        }

        List<Task> tasksList = new ArrayList<>(taskCount);
        while(!tasks.isEmpty()) tasksList.add(tasks.remove());

        return new TaskGenerator(tasksList);
    }
}
