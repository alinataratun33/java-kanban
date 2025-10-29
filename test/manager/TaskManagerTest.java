package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        manager = createTaskManager();
    }

    protected Task createTestTask() {
        return new Task(
                "Task",
                "Описание",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2025, 11,29, 15, 0)
        );
    }

    protected Epic createTestEpic() {
        return new Epic(
                "Epic",
                "Описание",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2025, 11,29, 15, 0)
        );
    }

    protected SubTask createTestSubTask(int epicId) {
        return new SubTask(
                "SubTask",
                "Описание",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2025, 11,29, 15, 0),
                epicId
        );
    }

    @Test
    public void testCreateTask() {
        Task createdTask = manager.createTask(createTestTask());
        assertEquals(1, manager.getAllTasks().size(), "Задача не создана");
    }

    @Test
    public void testCreateEpic() {
        Epic createdEpic = manager.createEpic(createTestEpic());
        assertEquals(1, manager.getAllEpics().size(), "Эпик не создан");
    }

    @Test
    public void testCreateSubTask() {
        Epic createdEpic = manager.createEpic(createTestEpic());
        manager.createSubTask(createTestSubTask(createdEpic.getId()));
        assertEquals(1, manager.getAllSubTasks().size(), "Подзадача не создана");
    }

    @Test
    public void testGetTaskById() {
        Task createdTask = manager.createTask(createTestTask());
        Task foundTask = manager.getTaskById(createdTask.getId());
        assertNotNull(foundTask, "Задача не найдена по ID");
        assertEquals(createdTask, foundTask, "Найденная задача не соответствует созданной");
    }

    @Test
    public void testGetEpicById() {
        Epic createdEpic = manager.createEpic(createTestEpic());

        Epic foundEpic = manager.getEpicById(createdEpic.getId());
        assertNotNull(foundEpic, "Эпик не найден по ID");
        assertEquals(createdEpic, foundEpic, "Найденный эпик не соответствует созданному");
    }

    @Test
    public void testGetSubTaskById() {
        Epic createdEpic = manager.createEpic(createTestEpic());
        SubTask createdSubTask = manager.createSubTask(createTestSubTask(createdEpic.getId()));

        SubTask foundSubTask = manager.getSubTaskById(createdSubTask.getId());
        assertNotNull(foundSubTask, "Подзадача не найдена по ID");
        assertEquals(createdSubTask, foundSubTask, "Найденная подзадача не соответствует созданной");
    }

    @Test
    public void testRemoveAllTasks() {
        manager.createTask(createTestTask());
        manager.createTask(createTestTask());
        assertFalse(manager.getAllTasks().isEmpty(), "Задачи должны существовать");
        manager.removeAllTasks();
        assertTrue(manager.getAllTasks().isEmpty(), "Задачи должны быть удалены");
    }

    @Test
    public void testRemoveAllEpics() {
        manager.createEpic(createTestEpic());
        manager.createEpic(createTestEpic());
        assertFalse(manager.getAllEpics().isEmpty(), "Эпики должны существовать");
        manager.removeAllEpics();
        assertTrue(manager.getAllEpics().isEmpty(), "Задачи должны быть удалены");
    }

    @Test
    public void testRemoveAllSubTasks() {
        Epic createdEpic = manager.createEpic(createTestEpic());
        manager.createSubTask(createTestSubTask(createdEpic.getId()));
        manager.createSubTask(createTestSubTask(createdEpic.getId()));

        assertFalse(manager.getAllSubTasks().isEmpty(), "Эпики должны существовать");
        manager.removeAllSubTasks();
        assertTrue(manager.getAllSubTasks().isEmpty(), "Задачи должны быть удалены");
    }

    @Test
    public void testUpdateTask() {
        Task createdTask = manager.createTask(createTestTask());
        Task updateTask = new Task("Измененная задача", "Измененное описание", Status.DONE,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11,29, 15, 0));
        updateTask.setId(createdTask.getId());
        manager.updateTask(updateTask);
        Task taskAfterUpdate = manager.getTaskById(1);

        assertEquals(1, manager.getAllTasks().size(), "В списке должна быть одна задача");
        assertEquals("Измененная задача", taskAfterUpdate.getName());
        assertEquals("Измененное описание", taskAfterUpdate.getDescription());
        assertEquals(Status.DONE, taskAfterUpdate.getStatus());
    }

    @Test
    public void testUpdateEpic() {
        Epic createdEpic = manager.createEpic(createTestEpic());
        Epic updateEpic = new Epic("Измененный эпик", "Измененное описание", Status.DONE,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11,29, 15, 0));
        updateEpic.setId(createdEpic.getId());
        manager.updateEpic(updateEpic);
        Epic epicAfterUpdate = manager.getEpicById(1);

        assertEquals(1, manager.getAllEpics().size(), "В списке должн быть один эпик");
        assertEquals("Измененный эпик", epicAfterUpdate.getName());
        assertEquals("Измененное описание", epicAfterUpdate.getDescription());
    }

    @Test
    public void testUpdateSubTask() {
        Epic createdEpic = manager.createEpic(createTestEpic());
        SubTask originalSubTask = manager.createSubTask(createTestSubTask(createdEpic.getId()));
        SubTask updateSubTask = new SubTask("Измененная подзадача", "Измененное описание",
                Status.DONE, Duration.ofMinutes(30),
                LocalDateTime.of(2025, 11,29, 15, 0),1);
        updateSubTask.setId(originalSubTask.getId());
        manager.updateSubTask(updateSubTask);
        SubTask subTaskAfterUpdate = manager.getSubTaskById(2);

        assertEquals(1, manager.getAllSubTasks().size(), "В списке должна быть одна подзадача");
        assertEquals("Измененная подзадача", subTaskAfterUpdate.getName());
        assertEquals("Измененное описание", subTaskAfterUpdate.getDescription());
        assertEquals(Status.DONE, subTaskAfterUpdate.getStatus());
    }

    @Test
    public void testRemoveTaskById() {
        Task taskForRemove = manager.createTask(createTestTask());
        manager.removeTaskById(taskForRemove.getId());
        assertTrue(manager.getAllTasks().isEmpty(), "Список задач должен быть пуст");
    }

    @Test
    public void testRemoveEpicById() {
        Epic epicForRemove = manager.createEpic(createTestEpic());
        manager.removeEpicById(epicForRemove.getId());
        assertTrue(manager.getAllEpics().isEmpty(), "Список эпиков должен быть пуст");
    }

    @Test
    public void testRemoveSubTaskById() {
        Epic createdEpic = manager.createEpic(createTestEpic());
        SubTask subTaskForRemove = manager.createSubTask(createTestSubTask(createdEpic.getId()));
        manager.removeSubTaskById(subTaskForRemove.getId());
        assertTrue(manager.getAllSubTasks().isEmpty(), "Список подзадач должен быть пуст");
    }
}
