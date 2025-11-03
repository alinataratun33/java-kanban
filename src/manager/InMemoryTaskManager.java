package manager;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, SubTask> subTasks;
    private final HistoryManager historyManager;
    private final TreeSet<Task> prioritizedTasks;
    private int count;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>(Task.START_TIME_COMPARATOR);
        count = 0;
    }

    protected void setCount(int count) {
        this.count = count;
    }

    private int generateId() {
        count++;
        return count;
    }

    @Override
    public Task createTask(Task task) {
        if (isConflict(task)) {
            throw new ManagerConflictException("Задача конфликтует с существующими задачами: " + task);
        }
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);

        prioritizedTasks.add(task);

        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        updateEpicStatus(epic);
        updateDateTimeEpic(epic);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        if (isConflict(subTask)) {
            throw new ManagerConflictException("Подзадача конфликтует с существующими задачами: " + subTask);
        }
        if (epics.containsKey(subTask.getEpicId())) {

            int id = generateId();
            subTask.setId(id);
            subTasks.put(id, subTask);

            if (subTask.getStartTime() != null) {
                prioritizedTasks.add(subTask);
            }

            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.addSubTaskId(id);
                updateEpicStatus(epic);
                updateDateTimeEpic(epic);
            }
        }
        return subTask;
    }

    @Override
    public List<Task> getAllTasks() {
        return tasks.values().stream().toList();
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return subTasks.values().stream().toList();
    }

    @Override
    public List<Epic> getAllEpics() {
        return epics.values().stream().toList();
    }

    @Override
    public void removeAllTasks() {
        tasks.values().forEach(task -> {
            prioritizedTasks.remove(task);
            historyManager.remove(task.getId());
        });
        tasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        subTasks.values().forEach(subTask -> {
            prioritizedTasks.remove(subTask);
            historyManager.remove(subTask.getId());
        });
        subTasks.clear();

        epics.values().forEach(epic -> {
            epic.clearSubTasks();
            updateEpicStatus(epic);
            updateDateTimeEpic(epic);
        });
    }

    @Override
    public void removeAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        epics.clear();

        removeAllSubTasks();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }


    @Override
    public SubTask getSubTaskById(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }


    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {

            if (isConflict(task)) {
                throw new ManagerConflictException("Обновленная задача конфликтует с существующими задачами: " + task);
            } else {
                prioritizedTasks.remove(tasks.get(task.getId()));
                tasks.put(task.getId(), task);
                if (task.getStartTime() != null) {
                    prioritizedTasks.add(task);
                }
            }
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {

        if (subTasks.containsKey(subTask.getId())) {
            SubTask currentSubTask = subTasks.get(subTask.getId());
            Integer currentEpicId = currentSubTask.getEpicId();
            Integer newEpicId = subTask.getEpicId();

            if (newEpicId.equals(currentEpicId)) {
                if (isConflict(subTask)) {
                    throw new ManagerConflictException("Обновленная подзадача конфликтует с существующими задачами: "
                            + subTask);
                } else {
                    prioritizedTasks.remove(currentSubTask);
                    subTasks.put(subTask.getId(), subTask);
                    prioritizedTasks.add(subTask);
                    Epic epic = epics.get(subTask.getEpicId());
                    updateEpicStatus(epic);
                    updateDateTimeEpic(epic);
                }
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic currentEpic = epics.get(epic.getId());
            currentEpic.setName(epic.getName());
            currentEpic.setDescription(epic.getDescription());
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (!tasks.containsKey(id)) {
            return;
        }
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic currentEpic = epics.get(id);
            currentEpic.getSubTaskIds()
                    .forEach(subId -> {
                        SubTask subTask = subTasks.remove(subId);
                        if (subTask != null) {
                            prioritizedTasks.remove(subTask);
                        }
                        historyManager.remove(subId);
                    });
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubTaskById(int id) {
        if (subTasks.containsKey(id)) {

            SubTask subTask = subTasks.get(id);
            int epicId = subTask.getEpicId();

            prioritizedTasks.remove(subTask);
            subTasks.remove(id);
            historyManager.remove(id);


            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.deleteSubTask(id);
                updateEpicStatus(epic);
                updateDateTimeEpic(epic);
            }
        }

    }

    @Override
    public List<SubTask> getSubTasksForEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return Collections.emptyList();
        }

        return epic.getSubTaskIds().stream()
                .map(subTasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Epic epic) {

        if (epic.getSubTaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean isDone = false;
        boolean isInProgress = false;
        boolean isNew = false;


        for (Integer subId : epic.getSubTaskIds()) {
            SubTask currentSubTask = subTasks.get(subId);
            Status status = currentSubTask.getStatus();
            if (status == Status.IN_PROGRESS) {
                isInProgress = true;
            }
            if (status == Status.NEW) {
                isNew = true;
            }
            if (status == Status.DONE) {
                isDone = true;
            }
        }
        if (isDone && !isInProgress && !isNew) {
            epic.setStatus(Status.DONE);
        } else if (isNew && !isDone && !isInProgress) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void updateDateTimeEpic(Epic epic) {
        epic.setStartTime(null);
        epic.setDuration(null);
        epic.endTime(null);

        LocalDateTime min = null;
        LocalDateTime max = null;
        Duration sumDuration = Duration.ZERO;

        for (Integer subId : epic.getSubTaskIds()) {
            SubTask currentSubTask = subTasks.get(subId);
            if (currentSubTask != null && currentSubTask.getStartTime() != null) {
                LocalDateTime startTime = currentSubTask.getStartTime();
                LocalDateTime endTime = startTime.plus(currentSubTask.getDuration());
                sumDuration = sumDuration.plus(currentSubTask.getDuration());
                if (min == null || startTime.isBefore(min)) {
                    min = startTime;
                }
                if (max == null || endTime.isAfter(max)) {
                    max = endTime;
                }
            }
        }
        epic.setDuration(sumDuration);
        epic.setStartTime(min);
        epic.endTime(max);
    }

    protected void addTaskFromFile(Task task) {
        switch (task.getType()) {
            case EPIC:
                Epic epic = (Epic) task;
                epics.put(epic.getId(), epic);
                epic.clearSubTasks();
                break;
            case SUBTASK:
                SubTask subTask = (SubTask) task;
                if (epics.containsKey(subTask.getEpicId())) {
                    subTasks.put(subTask.getId(), subTask);
                    Epic epicWithSubTask = epics.get(subTask.getEpicId());
                    if (epicWithSubTask != null) {
                        epicWithSubTask.addSubTaskId(subTask.getId());
                        updateEpicStatus(epicWithSubTask);
                        updateDateTimeEpic(epicWithSubTask);
                    }
                }
                break;
            case TASK:
                tasks.put(task.getId(), task);
                break;
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isTimeOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null && task2.getStartTime() == null) {
            return false;
        }
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private boolean isConflict(Task newTask) {
        if (newTask.getStartTime() == null) {
            return false;
        }
        List<Task> conflictingTasks = getPrioritizedTasks().stream()
                .filter(task -> task.getId() != newTask.getId())
                .filter(task -> isTimeOverlap(newTask, task))
                .collect(Collectors.toList());

        return !conflictingTasks.isEmpty();
    }
}







