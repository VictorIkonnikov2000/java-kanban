package tasks;


import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatus taskStatus, int epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, taskStatus,duration,startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
