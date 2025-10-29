package manager;

import tasks.Status;
import tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
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
    public void shouldReturnEmptyHistoryWhenNoTasksViewed() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой при инициализации");
    }

    @Test
    public void addTaskInHistory() {
        Task task = manager.createTask(new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11,29, 15, 0)));
        manager.getTaskById(task.getId());

        assertEquals(1, historyManager.getHistory().size(), "Задача не была добавлена в историю");
    }

    @Test
    public void removeTaskFromBeginning() {
        Task taskFirst = manager.createTask(new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11,29, 15, 0)));
        Task taskSecond = manager.createTask(new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11,28, 15, 0)));

        historyManager.add(taskFirst);
        historyManager.add(taskSecond);

        historyManager.remove(taskFirst.getId());


        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Задача не была удалена из истории");
        assertFalse(history.contains(taskFirst), "История не должна содержать удаленную задачу");
    }

    @Test
    public void removeTaskFromMiddle() {
        Task taskFirst = manager.createTask(new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11,29, 15, 0)));
        Task taskSecond = manager.createTask(new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11,28, 15, 0)));
        Task task = manager.createTask(new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11,27, 15, 0)));

        historyManager.add(taskFirst);
        historyManager.add(taskSecond);
        historyManager.add(task);

        historyManager.remove(taskSecond.getId());


        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Задача не была удалена из истории");
        assertFalse(history.contains(taskSecond), "История не должна содержать удаленную задачу");
    }

    @Test
    public void removeTaskFromMEnd() {
        Task taskFirst = manager.createTask(new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11,29, 15, 0)));

        Task taskSecond = manager.createTask(new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11,27, 15, 0)));

        historyManager.add(taskFirst);
        historyManager.add(taskSecond);

        historyManager.remove(taskSecond.getId());


        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Задача не была удалена из истории");
        assertFalse(history.contains(taskSecond), "История не должна содержать удаленную задачу");
    }

    @Test
    public void removeDuplicateViews() {
        Task task = manager.createTask(new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11,29, 15, 0)));
        manager.getTaskById(task.getId());
        manager.getTaskById(task.getId());

        assertEquals(1, historyManager.getHistory().size(), "Повторный просмотр не был удален");
    }


    @Test
    public void testHistoryPreservesModifiedVersion() {
        Task originalTask = manager.createTask(new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11,29, 15, 0)));
        historyManager.add(originalTask);

        Task updateTask = new Task("Измененная задача", "Описание изменено", Status.IN_PROGRESS,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11,29, 15, 0));
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