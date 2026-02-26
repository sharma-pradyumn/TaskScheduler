import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        TaskStore taskStore = new TaskStore();
        AtomicInteger printCount = new AtomicInteger();
        Schedule repeatedSchedulerSecond = new RepeatedExecutionScheduler(Duration.ofSeconds(20));
        Schedule repeatedSchedulerMin = new RepeatedExecutionScheduler(Duration.ofSeconds(10));
        Task simplePeriodicTask = new Task(()->{System.out.println("simplePeriodicTask"); printCount.getAndIncrement();},repeatedSchedulerSecond, LocalDateTime.now().plus(Duration.ofSeconds(10)));
        Task longerPeriodicTask = new Task(()->{System.out.println("longerPeriodicTask"); printCount.getAndIncrement();},repeatedSchedulerMin, LocalDateTime.now().plus(Duration.ofMinutes(1)));
        taskStore.addOrUpdateTask(simplePeriodicTask);
        taskStore.addOrUpdateTask(longerPeriodicTask);

        TaskScheduler taskScheduler = new TaskScheduler(taskStore, 1);
        taskScheduler.start();

        while (printCount.get() < 10){
            ;
        }
        taskScheduler.stop();


    }
}


//1. Functional Requirements (The "What")
//One-time Execution: Schedule a task to run at a specific LocalDateTime.
//Recurring Execution: Schedule a task to run at fixed intervals (e.g., every 5 minutes) starting from a specific time.
//Prioritization: If two tasks are due at the same time, the one with the higher priority (e.g., 1 vs 10) must run first.
//Persistence (Simulated): The ability to addTask, removeTask, and listTasks.
//Execution Lifecycle: A task must track its status: PENDING, RUNNING, SUCCESS, FAILED.
//
//2. Practical "Backend" Requirements (The "OOD")
//Strategy Pattern for Scheduling: Decouple the "Task" from the "When it runs." A task shouldn't care if it's a one-timer or a CRON job.
//Retry Logic: If a task fails, it should have a RetryStrategy. (e.g., "Retry 3 times" or "No Retry").
//Execution Isolation: If the logic inside a task throws an exception, the Scheduler should catch it, update the task status to FAILED, and move on to the next task without crashing.
//Thread-Safety (Light): Even if we aren't going deep on concurrency, your TaskStore (the list/queue of tasks) should be able to handle someone adding a task while the scheduler is reading tasks.
//
//3. Suggested Class Responsibilities
//To hit those SOLID principles, think about these four buckets:
//Task: The entity. Holds the Runnable command, priority, id, and current status.
//Schedule (Interface): Has a method getNextExecutionTime(LocalDateTime lastRun).
//Impls: OneTimeSchedule, IntervalSchedule.
//RetryStrategy (Interface): Has a method shouldRetry(int currentAttempt).
//Scheduler: The engine. Manages the PriorityQueue and the loop that checks for due tasks.
//
//4. Java Implementation "Watch-Outs"
//Since you mentioned the "Syntax Friction," keep these in your back pocket for your attempt:
//PriorityQueue Constructor:

// Comparing by time, then by priority (lower number = higher priority)
//PriorityQueue<Task> pq = new PriorityQueue<>(
//        Comparator.comparing(Task::getNextRunTime)
//                .thenComparingInt(Task::getPriority));
//Queue Methods:
//        pq.peek(): See the next task due.
//        pq.poll(): Take the task out to run it.
//        pq.add(): Put it back in (if it needs to recur or retry).
//
//Time Comparison: taskTime.isBefore(LocalDateTime.now()) or isAfter().
