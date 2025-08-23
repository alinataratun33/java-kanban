import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager manager;

    private Task task;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    public void setUp() {
        manager = Managers.getDefault();
        initTestData();
    }

    private void initTestData() {
        task = createTestTask();
        epic = createTestEpic();
        subTask = createTestSubTask(epic.getId());
    }

    private Task createTestTask() {
        return new Task(
                "Task",
                "Описание",
                Status.NEW
        );
    }

    private Epic createTestEpic() {
        return new Epic(
                "Epic",
                "Описание",
                Status.NEW
        );
    }

    private SubTask createTestSubTask(int epicId) {
        return new SubTask(
                "SubTask",
                "Описание",
                Status.NEW,
                epicId
        );
    }

    @Test
    public void testCreateTask() {
        manager.createTask(task);
        assertEquals(1, manager.getAllTasks().size(), "Задача не создана");
    }

    @Test
    public void testCreateEpic() {
        manager.createEpic(epic);
        assertEquals(1, manager.getAllEpics().size(), "Эпик не создан");
    }

    @Test
    public void testCreateSubTask() {
        Epic createdEpic = manager.createEpic(epic);
        manager.createSubTask(createTestSubTask(createdEpic.getId()));
        assertEquals(1, manager.getAllSubTasks().size(), "Подзадача не создана");
    }

    @Test
    public void testGetTaskById() {
        Task createdTask = manager.createTask(task);
        Task foundTask = manager.getTaskById(createdTask.getId());
        assertNotNull(foundTask, "Задача не найдена по ID");
        assertEquals(createdTask, foundTask, "Найденная задача не соответствует созданной");
    }

    @Test
    public void testGetEpicById() {
        Epic createdEpic = manager.createEpic(epic);

        Epic foundEpic = manager.getEpicById(createdEpic.getId());
        assertNotNull(foundEpic, "Эпик не найден по ID");
        assertEquals(createdEpic, foundEpic, "Найденный эпик не соответствует созданному");
    }

    @Test
    public void testGetSubTaskById() {
        Epic createdEpic = manager.createEpic(epic);
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
        Epic createdEpic = manager.createEpic(epic);
        manager.createSubTask(createTestSubTask(createdEpic.getId()));
        manager.createSubTask(createTestSubTask(createdEpic.getId()));

        assertFalse(manager.getAllSubTasks().isEmpty(), "Эпики должны существовать");
        manager.removeAllSubTasks();
        assertTrue(manager.getAllSubTasks().isEmpty(), "Задачи должны быть удалены");
    }

    @Test
    public void testUpdateTask() {
        Task originalTask = manager.createTask(task);
        Task updateTask = new Task("Измененная задача", "Измененное описание", Status.DONE);
        updateTask.setId(originalTask.getId());
        manager.updateTask(updateTask);
        Task taskAfterUpdate = manager.getTaskById(1);

        assertEquals(1, manager.getAllTasks().size(), "В списке должна быть одна задача");
        assertEquals("Измененная задача", taskAfterUpdate.getName());
        assertEquals("Измененное описание", taskAfterUpdate.getDescription());
        assertEquals(Status.DONE, taskAfterUpdate.getStatus());
    }

    @Test
    public void testUpdateEpic() {
        Epic originalEpic = manager.createEpic(epic);
        Epic updateEpic = new Epic("Измененный эпик", "Измененное описание", Status.DONE);
        updateEpic.setId(originalEpic.getId());
        manager.updateEpic(updateEpic);
        Epic epicAfterUpdate = manager.getEpicById(1);

        assertEquals(1, manager.getAllEpics().size(), "В списке должн быть один эпик");
        assertEquals("Измененный эпик", epicAfterUpdate.getName());
        assertEquals("Измененное описание", epicAfterUpdate.getDescription());
    }

    @Test
    public void testUpdateSubTask() {
        Epic createdEpic = manager.createEpic(epic);
        SubTask originalSubTask = manager.createSubTask(createTestSubTask(createdEpic.getId()));
        SubTask updateSubTask = new SubTask("Измененная подзадача", "Измененное описание",
                Status.DONE,1);
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
        Task taskForRemove = manager.createTask(task);
        manager.removeTaskById(taskForRemove.getId());
        assertTrue(manager.getAllTasks().isEmpty(), "Список задач должен быть пуст");
    }

    @Test
    public void testRemoveEpicById() {
        Epic epicForRemove = manager.createEpic(epic);
        manager.removeEpicById(epicForRemove.getId());
        assertTrue(manager.getAllEpics().isEmpty(), "Список эпиков должен быть пуст");
    }

    @Test
    public void testRemoveSubTaskById() {
        Epic createdEpic = manager.createEpic(epic);
        SubTask subTaskForRemove = manager.createSubTask(createTestSubTask(createdEpic.getId()));
        manager.removeSubTaskById(subTaskForRemove.getId());
        assertTrue(manager.getAllSubTasks().isEmpty(), "Список подзадач должен быть пуст");
    }

    @Test
    void testTasksWithPredefinedAndGeneratedIdsDoNotConflict() {
        Task autoIdTask = manager.createTask(task);
        Task taskWithSetId = new Task("Задача с заданным id", "Описание", Status.NEW);
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

        manager.createEpic(epic);

        SubTask originalSubTask = createTestSubTask(epic.getId());
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


}