import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, SubTask> subTasks;
    private static int count;

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
        epic.setStatus(Status.NEW);
        epics.put(id, epic);
        return epic;
    }

    public SubTask createSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId())) {

            int id = generateId();
            subTask.setId(id);
            subTasks.put(id, subTask);

            Epic epic = epics.get(subTask.getEpicId());
            epic.addSubTaskId(id);
            updateEpicStatus(subTask);
        }
        return subTask;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();

        for (Task t : tasks.values()) {
            allTasks.add(t);
        }
        for (Task e : epics.values()) {
            allTasks.add(e);
        }
        for (Task s : subTasks.values()) {
            allTasks.add(s);
        }
        return allTasks;
    }

    public void removeAllTask() {
        tasks.clear();
        epics.clear();
        subTasks.clear();
        count = 0;
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            return epics.get(id);
        } else if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        } else {
            return null;
        }
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())){
            subTasks.put(subTask.getId(), subTask);
            updateEpicStatus(subTask);}
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    public void removeTaskById(int id) {

        tasks.remove(id);}


    public void removeEpicById(int id) {
        ArrayList<Integer> subTaskForRemove = new ArrayList<>();
        if (epics.containsKey(id)) {
            for (SubTask s : subTasks.values()) {
                if (s.getEpicId() == id) {
                    subTaskForRemove.add(s.getId());
                }
            }
            for (Integer subId : subTaskForRemove) {
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
                ArrayList<Integer> subId = epic.getSubTaskIds();
                subId.remove((Object) id);

                updateEpicStatus(subTask);
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

    private void updateEpicStatus(SubTask subTask) {
        boolean isDone = false;
        boolean isInProgress = false;
        boolean isNew = false;
        Epic epic = epics.get(subTask.getEpicId());
        ArrayList<Integer> listSubId = epic.getSubTaskIds();

        for (Integer subId : listSubId) {
            SubTask subTaskCurrent = subTasks.get(subId);
            Status status = subTaskCurrent.getStatus();
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
