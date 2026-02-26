import java.time.LocalDateTime;

public interface Schedule {
    public LocalDateTime getNextExecutionTime(LocalDateTime previousExecutionTime);
}
