package Manager;

import Tasks.Status;
import Tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private TaskManager manager;

    @BeforeEach
    public void setUp() {
        historyManager = Managers.getDefaultHistory();
        manager = Managers.getDefault();
    }

    @Test
    public void addTaskInHistory() {
        Task task = manager.createTask(new Task("Задача", "Описание", Status.NEW));
        manager.getTaskById(task.getId());

        assertEquals(1, historyManager.getHistory().size(), "Задача не была добавлена в историю");
    }

    @Test
    public void removeTaskFromHistory() {
        Task task = manager.createTask(new Task("Задача", "Описание", Status.NEW));
        manager.getTaskById(task.getId());
        manager.removeTaskById(task.getId());

        assertEquals(0, historyManager.getHistory().size(), "Задача не была удалена из истории");
    }

    @Test
    public void removeDuplicateViews() {
        Task task = manager.createTask(new Task("Задача", "Описание", Status.NEW));
        manager.getTaskById(task.getId());
        manager.getTaskById(task.getId());

        assertEquals(1, historyManager.getHistory().size(), "Повторный просмотр не был удален");
    }


    @Test
    public void testHistoryPreservesModifiedVersion() {
        Task originalTask = manager.createTask(new Task("Задача", "Описание", Status.NEW));
        historyManager.add(originalTask);

        Task updateTask = new Task("Измененная задача", "Описание изменено", Status.IN_PROGRESS);
        updateTask.setId(originalTask.getId());
        manager.updateTask(updateTask);

        historyManager.add(updateTask);

        List<Task> history = historyManager.getHistory();


        Task updateVersion = history.getLast();

        assertEquals(1, history.size(), "Список должен содержать две версии");

        assertEquals(updateTask.getName(), updateVersion.getName(), "Имя измененной версии не совпадает");
        assertEquals(updateTask.getDescription(),
                updateVersion.getDescription(), "Описание измененной версии не совпадает");
        assertEquals(updateTask.getStatus(), updateVersion.getStatus(), "Статус измененной версии не совпадает");
    }
}