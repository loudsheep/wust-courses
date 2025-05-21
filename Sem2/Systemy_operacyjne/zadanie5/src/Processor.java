import java.util.LinkedList;

public class Processor {
    private int id;
    private int currentUtilization;

    private final LinkedList<Task> processQueue = new LinkedList<>();
    private Task currentTask;

    public Processor(int id) {
        this.id = id;
        this.currentUtilization = 0;
    }

    public void tick() {
        this.checkNextTask();

        if (this.currentTask == null) return;

        this.currentTask.tick();

        if (this.currentTask.getExecTimeLeft() <= 0) {
            // TODO: stat service tick
            this.currentTask = null;
        }
    }

    public void addTaskToQueue(Task task) {
        this.processQueue.add(task);
    }

    private void checkNextTask() {
        if (this.currentTask != null || this.processQueue.isEmpty()) return;

        this.currentTask = this.processQueue.remove();
    }
}
