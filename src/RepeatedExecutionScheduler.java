import java.time.Duration;
import java.time.LocalDateTime;

public class RepeatedExecutionScheduler implements Schedule{
    private final Duration timeInterval;

    RepeatedExecutionScheduler(Duration interval) {
        timeInterval = interval;
    }

    @Override
    public LocalDateTime getNextExecutionTime(LocalDateTime previousExecutionTime) {
//        System.out.println("[previousExecutionTime]: "+previousExecutionTime+" [next]: "+previousExecutionTime.plus(timeInterval));
        return previousExecutionTime.plus(timeInterval);
    }
}
