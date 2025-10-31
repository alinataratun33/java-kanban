package tasks;

import manager.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String description, Status status, Duration duration, LocalDateTime startTime, int epicId) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }


    public int getEpicId() {
        return epicId;
    }

    public TypeTask getType() {
        return TypeTask.SUBTASK;
    }

    @Override
    public String toString() {
        return super.toString()+ "," + epicId;
    }
}
