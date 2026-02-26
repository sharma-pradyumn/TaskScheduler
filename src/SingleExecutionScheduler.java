import java.time.LocalDateTime;

public class SingleExecutionScheduler implements Schedule {

    // null indicates no more execution
    @Override
    public LocalDateTime getNextExecutionTime(LocalDateTime previousExecutionTime) {
        return null;
    }
}
