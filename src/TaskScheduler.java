import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskScheduler {
    private final TaskStore taskStore;
    private final ExecutorService workerPool;
    private volatile boolean running = true;
    private int MAX_SHUTDOWN_TIME = 10_000;

    public TaskScheduler(TaskStore taskStore, int poolSize) {
        this.taskStore = taskStore;
        // The Worker Pool: where the actual work happens
        this.workerPool = Executors.newFixedThreadPool(poolSize);
    }

    public void start() {
        // orchestrator
       Thread orchestrator = new Thread(()->{
           while (running) {
               Task t = taskStore.pollNextAvailableTask();
               if (t == null) {
//                   try {
//                       Thread.sleep(100);
//                   } catch (InterruptedException e) {
//                       Thread.currentThread().interrupt();
//                   }
                   continue;
               }
               // A task IS ready! Hand it to the worker pool.
               workerPool.submit(()->executeTask(t));
           }
       });
        orchestrator.setName("Task-Orchestrator");
        orchestrator.start();
    }

    private void executeTask(Task t) {
       if (t == null) {
           return;
       }
       t.setTaskStatus(TaskStatus.RUNNING);
       try {
           t.getCommand().run();
       } catch (Exception e) {
           t.setTaskStatus(TaskStatus.ERROR);
           System.err.println("Exception when running task: "+e.getMessage());
       }
       boolean shouldContinue = t.updateNextExecutionTime();
       t.setTaskStatus( shouldContinue ? TaskStatus.PENDING: TaskStatus.FINISHED);
       if (shouldContinue) {
           taskStore.addOrUpdateTask(t);
       }
    }

    public void stop() {
        running = false;
        workerPool.shutdown();
        try {
            if (!workerPool.awaitTermination(MAX_SHUTDOWN_TIME, TimeUnit.SECONDS)) {
                workerPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            workerPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
