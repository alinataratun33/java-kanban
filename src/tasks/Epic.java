package tasks;

import manager.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {

    private final List<Integer> subTaskIds;
    protected LocalDateTime endTime;

    public Epic(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.subTaskIds = new ArrayList<>();
    }


    public List<Integer> getSubTaskIds() {
        return new ArrayList<>(subTaskIds);
    }

    public void deleteSubTask(Integer subTaskId) {
        if (!subTaskIds.contains(subTaskId)) {
            return;
        }
        subTaskIds.remove(subTaskId);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void clearSubTasks() {
        subTaskIds.clear();
    }


    public void addSubTaskId(int subTaskId) {
        if (subTaskId == getId()) {
            return;
        }
        if (!subTaskIds.contains(subTaskId)) {
            subTaskIds.add(subTaskId);
        }
    }

    public TypeTask getType() {
        return TypeTask.EPIC;
    }

    public void endTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
        return String.format("%d,%s,%s,%s,%s,%d,%s,",
                getId(), getType(), getName(), getStatus(), getDescription(),
                durationMinutes, startTimeStr);
    }
}
