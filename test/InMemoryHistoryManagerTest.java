import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

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
    public void testHistoryPreservesOriginalVersion() {
        Task originalTask = manager.createTask(new Task("Задача", "Описание", Status.NEW));
        historyManager.add(originalTask);

        Task updateTask = new Task("Измененная задача", "Описание изменено", Status.IN_PROGRESS);
        updateTask.setId(originalTask.getId());
        manager.updateTask(updateTask);

        historyManager.add(updateTask);

        ArrayList<Task> history = historyManager.getHistory();

        Task firstVersion = history.getFirst();
        Task updateVersion = history.getLast();

        assertEquals(2, history.size(), "Список должен содержать две версии");
        assertEquals(originalTask.getName(), firstVersion.getName(), "Имя первой версии не совпадает");
        assertEquals(originalTask.getDescription(),
                firstVersion.getDescription(), "Описание первой версии не совпадает");
        assertEquals(originalTask.getStatus(), firstVersion.getStatus(), "Статус первой версии не совпадает");

        assertEquals(updateTask.getName(), updateVersion.getName(), "Имя второй версии не совпадает");
        assertEquals(updateTask.getDescription(),
                updateVersion.getDescription(), "Описание второй версии не совпадает");
        assertEquals(updateTask.getStatus(), updateVersion.getStatus(), "Статус втрой версии не совпадает");
    }
}