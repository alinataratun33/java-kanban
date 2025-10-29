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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        String startTimeStr = "";
        if (getStartTime() != null) {
            startTimeStr = getStartTime().format(formatter);
        }

        long durationMinutes = 0;
        if (getDuration() != null) {
            durationMinutes = getDuration().toMinutes();
        }
        return String.format("%d,%s,%s,%s,%s,%d,%s,%d",
                getId(), getType(), getName(), getStatus(), getDescription(),
                durationMinutes, startTimeStr, epicId);
    }
}
