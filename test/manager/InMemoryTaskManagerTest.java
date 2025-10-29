package manager;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11,29, 15, 0));
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

        assertNull(manager.getSubTaskById(idSubTaskForRemove), "ID удаленной подзадачи сохранилось");
    }

    @Test
    void testOverlap(){
        Task createdTask = manager.createTask(createTestTask());
        Task createdTask2 = manager.createTask(createTestTask());

        assertEquals(1, manager.getAllTasks().size(),
                "В списке находятся задачи с пересекающимся временем");
    }

}