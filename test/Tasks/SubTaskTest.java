package Tasks;

import Manager.Managers;
import Manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    TaskManager manager;


    @BeforeEach
    public void setUp() {
        manager = Managers.getDefault();
    }

    @Test
    public void shouldReturnEqualsTwoSubTasks() {
        SubTask subTaskOne = new SubTask("Task.SubTask", "Description", Status.NEW, 1);
        subTaskOne.setId(1);
        SubTask subTaskTwo = new SubTask("Task.SubTask", "Description", Status.NEW, 1);
        subTaskTwo.setId(1);
        assertEquals(subTaskOne, subTaskTwo, "Объекты не равны");
    }

    @Test
    public void subTaskCannotBeEpic() {
        SubTask subTask = new SubTask("Task.SubTask", "Description", Status.NEW, 1);
        manager.createSubTask(subTask);
        assertTrue(manager.getAllSubTasks().isEmpty(), "Подзадача добавлена как эпик");
    }
}