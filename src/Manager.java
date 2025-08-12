import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;
    private int count;

    public Manager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        count = 0;
    }

    public int generateId() {
        count++;
        return count;
    }

    public Task createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        updateEpicStatus(epic);
        epics.put(id, epic);
        return epic;
    }

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

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();

        for (Task t : tasks.values()) {
            allTasks.add(t);
        }
        return allTasks;
    }

    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> allSubTasks = new ArrayList<>();
        for (SubTask s : subTasks.values()) {
            allSubTasks.add(s);
        }
        return allSubTasks;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        for (Epic e : epics.values()) {
            allEpics.add(e);
        }
        return allEpics;
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
        }
    }

    public void removeAllEpics() {
        epics.clear();
        removeAllSubTasks();
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }


    public SubTask getSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        }
        return null;
    }

    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }


    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

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

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic currentEpic = epics.get(epic.getId());
            currentEpic.setName(epic.getName());
            currentEpic.setDescription(epic.getDescription());
        }
    }

    public void removeTaskById(int id) {
        if (!tasks.containsKey(id)) {
            return;
        }
        tasks.remove(id);
    }


    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic currentEpic = epics.get(id);
            for (Integer subId : currentEpic.getSubTaskIds()) {
                subTasks.remove(subId);
            }
            epics.remove(id);
        }
    }

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


    public ArrayList<SubTask> getSubTasksForEpic(Epic epic) {
        ArrayList<SubTask> result = new ArrayList<>();

        for (Integer subTaskId : epic.getSubTaskIds()) {
            SubTask subTask = subTasks.get(subTaskId);
            if (subTask != null) {
                result.add(subTask);
            }
        }
        return result;
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






