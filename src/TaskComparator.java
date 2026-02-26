import java.time.LocalDateTime;
import java.util.Comparator;

public final class TaskComparator implements Comparator<Task> {

    @Override
    public int compare(Task t1, Task t2) {
        int timeCompare = t1.getNextExecutionTime().compareTo(t2.getNextExecutionTime());
        if (timeCompare == 0) {
            return Integer.compare(t1.getPriority(), t2.getPriority());
        }
        return timeCompare;
    }
}
