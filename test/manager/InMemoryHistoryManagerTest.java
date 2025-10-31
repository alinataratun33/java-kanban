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

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldReturnEmptyHistoryWhenNoTasksViewed() {
        List<Task> history = historyManager.getHistory();
        for (Task task : history) {
            System.out.println(task);
        }
        assertTrue(history.isEmpty(), "История должна быть пустой при инициализации");
    }

    @Test
    public void addTaskInHistory() {
        Task task = new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11, 29, 15, 0));
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size(), "Задача не была добавлена в историю");
    }

    @Test
    public void removeTaskFromBeginning() {
        Task taskFirst = new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11, 29, 15, 0));
        taskFirst.setId(1);
        Task taskSecond = new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11, 28, 15, 0));
        taskSecond.setId(2);

        historyManager.add(taskFirst);
        historyManager.add(taskSecond);

        historyManager.remove(1);


        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "Задача не была удалена из истории");
        assertFalse(history.contains(taskFirst), "История не должна содержать удаленную задачу");
    }

    @Test
    public void removeTaskFromMiddle() {
        Task taskFirst = new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11, 29, 15, 0));
        taskFirst.setId(1);
        Task taskSecond = new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11, 28, 15, 0));
        taskSecond.setId(2);
        Task task = new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11, 27, 15, 0));
        task.setId(3);

        historyManager.add(taskFirst);
        historyManager.add(taskSecond);
        historyManager.add(task);

        historyManager.remove(2);


        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Задача не была удалена из истории");
        assertFalse(history.contains(taskSecond), "История не должна содержать удаленную задачу");
    }

    @Test
    public void removeTaskFromMEnd() {
        Task taskFirst = new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11, 29, 15, 0));
        taskFirst.setId(1);

        Task taskSecond = new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11, 27, 15, 0));
        taskSecond.setId(2);

        historyManager.add(taskFirst);
        historyManager.add(taskSecond);

        historyManager.remove(2);


        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Задача не была удалена из истории");
        assertFalse(history.contains(taskSecond), "История не должна содержать удаленную задачу");
    }

    @Test
    public void removeDuplicateViews() {
        Task task = new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11, 29, 15, 0));
        historyManager.add(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size(), "Повторный просмотр не был удален");
    }


    @Test
    public void testHistoryPreservesModifiedVersion() {
        Task originalTask = new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11, 29, 15, 0));
        historyManager.add(originalTask);

        Task updateTask = new Task("Измененная задача", "Описание изменено", Status.IN_PROGRESS,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11, 29, 15, 0));
        updateTask.setId(originalTask.getId());


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