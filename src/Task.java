import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class Task {
    private Runnable command;
    private int priority;
    private final int taskId;
    private Schedule taskSchedule;
    private TaskStatus taskStatus;
    static AtomicInteger nextTaskId = new AtomicInteger(1);
    private LocalDateTime nextExecutionTime;

    public Task(Runnable c, Schedule ts, LocalDateTime firstTime, int p) {
        command = c;
        priority = p;
        taskSchedule = ts;
        taskStatus = TaskStatus.PENDING;
        nextExecutionTime = firstTime;
        taskId = nextTaskId.getAndIncrement();
    }

    public Task(Runnable c, Schedule ts, LocalDateTime firstTime) {
        this(c, ts, firstTime, 1);
    }

    public int getTaskId() {
        return taskId;
    }

    public int getPriority() {
        return priority;
    }

    public Runnable getCommand() {
        return command;
    }

    public Schedule getTaskSchedule() {
        return taskSchedule;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public LocalDateTime getNextExecutionTime() {
        return nextExecutionTime;
    }

    public void setCommand(Runnable command) {
        this.command = command;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setTaskSchedule(Schedule taskSchedule) {
        this.taskSchedule = taskSchedule;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public boolean updateNextExecutionTime() {
        nextExecutionTime = taskSchedule.getNextExecutionTime(this.nextExecutionTime);
        return nextExecutionTime != null;
    }
}
