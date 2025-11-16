package manager;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void testTasksWithPredefinedAndGeneratedIdsDoNotConflict() {
        Task autoIdTask = manager.createTask(createTestTask());
        Task taskWithSetId = new Task("Задача с заданным id", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11, 29, 15, 0));
        taskWithSetId.setId(3);
        manager.createTask(taskWithSetId);
        assertNotEquals(taskWithSetId.getId(), autoIdTask.getId(), "ID задач конфликтуют");
    }

    @Test
    void testTaskUnchangedAfterAddingToManager() {
        Task originalTask = createTestTask();
        String originalName = originalTask.getName();
        String originalDescription = originalTask.getDescription();
        Status originalStatus = originalTask.getStatus();

        manager.createTask(originalTask);

        Task taskAfterAdding = manager.getTaskById(1);

        assertNotNull(taskAfterAdding, "Задача должна быть найдена");
        assertEquals(taskAfterAdding.getName(), originalName, "Имена не совпадают");
        assertEquals(taskAfterAdding.getDescription(), originalDescription, "Описания не совпадают");
        assertEquals(taskAfterAdding.getStatus(), originalStatus, "Статусы не совпадают");
    }

    @Test
    void testEpicUnchangedAfterAddingToManager() {
        Epic originalEpic = createTestEpic();
        String originalName = originalEpic.getName();
        String originalDescription = originalEpic.getDescription();
        Status originalStatus = originalEpic.getStatus();

        manager.createEpic(originalEpic);

        Epic epicAfterAdding = manager.getEpicById(1);

        assertNotNull(epicAfterAdding, "Эпик должн быть найден");
        assertEquals(epicAfterAdding.getName(), originalName, "Имена не совпадают");
        assertEquals(epicAfterAdding.getDescription(), originalDescription, "Описания не совпадают");
        assertEquals(epicAfterAdding.getStatus(), originalStatus, "Статусы не совпадают");
    }

    @Test
    void testSubTaskUnchangedAfterAddingToManager() {

        Epic createdEpic = manager.createEpic(createTestEpic());

        SubTask originalSubTask = createTestSubTask(createdEpic.getId());
        String originalName = originalSubTask.getName();
        String originalDescription = originalSubTask.getDescription();
        Status originalStatus = originalSubTask.getStatus();
        int originalEpicId = originalSubTask.getEpicId();

        SubTask subTaskAfterAdding = manager.createSubTask(originalSubTask);

        assertNotNull(originalSubTask, "Подзадача должна быть найдена");
        assertEquals(subTaskAfterAdding.getName(), originalName, "Имена не совпадают");
        assertEquals(subTaskAfterAdding.getDescription(), originalDescription, "Описания не совпадают");
        assertEquals(subTaskAfterAdding.getStatus(), originalStatus, "Статусы не совпадают");
        assertEquals(subTaskAfterAdding.getEpicId(), originalEpicId, "ID не совпадают");
    }

    @Test
    void testEpicDoNotContainIrrelevantSubtasksIDs() {
        Epic createdEpic = manager.createEpic(createTestEpic());
        SubTask subTaskForRemove = manager.createSubTask(createTestSubTask(createdEpic.getId()));
        manager.removeSubTaskById(subTaskForRemove.getId());

        assertEquals(0, createdEpic.getSubTaskIds().size(), "Эпик содержит неактуальные id подзадач");
    }

    @Test
    void testRemoveSubTaskId() {
        Epic createdEpic = manager.createEpic(createTestEpic());
        SubTask subTaskForRemove = manager.createSubTask(createTestSubTask(createdEpic.getId()));
        int idSubTaskForRemove = subTaskForRemove.getId();
        manager.removeSubTaskById(idSubTaskForRemove);

        Epic updatedEpic = manager.getEpicById(createdEpic.getId());


        assertFalse(updatedEpic.getSubTaskIds().contains(idSubTaskForRemove),
                "Эпик не должен содержать ID удаленной подзадачи");
    }

    @Test
    void testOverlap() {
        Task task1 = createTestTask();
        Task task2 = createTestTask();


        Task createdTask1 = manager.createTask(task1);
        assertNotNull(createdTask1, "Первая задача должна создаться");

        assertThrows(ManagerConflictException.class, () -> {
            manager.createTask(task2);
        }, "Должно броситься исключение при создании задачи с пересекающимся временем");


        assertEquals(1, manager.getAllTasks().size(),
                "В списке должна остаться только одна задача");
        assertEquals(1, manager.getAllTasks().size(),
                "В списке должна остаться только одна задача");
    }

}