package Manager;

import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;

import java.util.List;

public interface TaskManager {

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask);

    List<Task> getAllTasks();

    List<SubTask> getAllSubTasks();

    List<Epic> getAllEpics();

    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpics();

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubTaskById(int id);

    List<SubTask> getSubTasksForEpic(int epicId);

    List<Task> getHistory();

}
