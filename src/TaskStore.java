import java.time.LocalDateTime;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class TaskStore {
    PriorityQueue<Task> taskPriorityQueue = new PriorityQueue<Task>(new TaskComparator());
    Map<Integer, Task> taskMap = new ConcurrentHashMap<>();

    public synchronized void addOrUpdateTask(Task t){
        taskMap.put(t.getTaskId(), t);
        taskPriorityQueue.offer(t);
    }

    // returns the task that is pending and can be executed at currentTime
    public synchronized Task pollNextAvailableTask(){
        while (!taskPriorityQueue.isEmpty()) {
            Task t = taskPriorityQueue.poll();
            if(t == null || t.getTaskStatus() != TaskStatus.PENDING) {
                continue;
            }
            Task latestTaskVersion = taskMap.get(t.getTaskId());
            // If the task was cancelled, or if the instance in PQ is NOT the latest instance
            // (e.g., someone updated priority/time, creating a new Task instance in PQ)
            if (latestTaskVersion == null || t != latestTaskVersion) {
                continue; // Discard stale/cancelled task and check the next one in PQ
            }
            if (t.getNextExecutionTime().isAfter(LocalDateTime.now())) {
                taskPriorityQueue.add(t);
                return null;
            }
            // remove task to remove stale entries
            removeTask(t.getTaskId());
            return t;
        }
        return null;
    }

    public void removeTask(Integer taskId) {
        taskMap.remove(taskId);
    }

}
