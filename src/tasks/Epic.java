package tasks;

import manager.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {

    private List<Integer> subTaskIds;
    private LocalDateTime endTime;

    public Epic(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.subTaskIds = new ArrayList<>();
    }


    public List<Integer> getSubTaskIds() {
        if (subTaskIds == null) {
            subTaskIds = new ArrayList<>();
        }
        return subTaskIds;
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
}
