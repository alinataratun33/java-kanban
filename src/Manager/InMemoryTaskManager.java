package Manager;

import Tasks.Epic;
import Tasks.Status;
import Tasks.SubTask;
import Tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, SubTask> subTasks;
    private final HistoryManager historyManager;

    private int count;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        count = 0;
    }

    private int generateId() {
        count++;
        return count;
    }

    @Override
    public Task createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        updateEpicStatus(epic);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId())) {

            int id = generateId();
            subTask.setId(id);
            subTasks.put(id, subTask);

            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.addSubTaskId(id);
                updateEpicStatus(epic);
            }
        }
        return subTask;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();

        for (Task t : tasks.values()) {
            allTasks.add(t);
        }
        return allTasks;
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        List<SubTask> allSubTasks = new ArrayList<>();
        for (SubTask s : subTasks.values()) {
            allSubTasks.add(s);
        }
        return allSubTasks;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> allEpics = new ArrayList<>();
        for (Epic e : epics.values()) {
            allEpics.add(e);
        }
        return allEpics;
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void removeAllEpics() {
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
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {

        if (subTasks.containsKey(subTask.getId())) {
            SubTask currentSubTask = subTasks.get(subTask.getId());
            Integer currentEpicId = currentSubTask.getEpicId();
            Integer newEpicId = subTask.getEpicId();

            if (newEpicId.equals(currentEpicId)) {
                subTasks.put(subTask.getId(), subTask);
                Epic epic = epics.get(subTask.getEpicId());
                updateEpicStatus(epic);
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
        tasks.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic currentEpic = epics.get(id);
            for (Integer subId : currentEpic.getSubTaskIds()) {
                subTasks.remove(subId);
            }
            epics.remove(id);
        }
    }

    @Override
    public void removeSubTaskById(int id) {
        if (subTasks.containsKey(id)) {

            SubTask subTask = subTasks.get(id);
            int epicId = subTask.getEpicId();

            subTasks.remove(id);

            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.deleteSubTask(id);
                updateEpicStatus(epic);
            }
        }
    }


    @Override
    public List<SubTask> getSubTasksForEpic(int epicId) {
        Epic epic = epics.get(epicId);

        List<SubTask> result = new ArrayList<>();
        if (epic != null) {
            for (Integer subTaskId : epic.getSubTaskIds()) {
                SubTask subTask = subTasks.get(subTaskId);
                if (subTask != null) {
                    result.add(subTask);
                }
            }
        }
        return result;

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
}






