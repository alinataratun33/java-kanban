package tasks;

import manager.Managers;
import manager.TaskManager;
import manager.TypeTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    TaskManager manager;


    @BeforeEach
    public void setUp() {
        manager = Managers.getDefault();
    }

    @Test
    public void shouldReturnEqualsTwoSubTasks() {
        SubTask subTaskOne = new SubTask("SubTask", "Description", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11,29, 15, 0),
                1);
        subTaskOne.setId(1);
        SubTask subTaskTwo = new SubTask("SubTask", "Description", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11,29, 15, 0),
                1);
        subTaskTwo.setId(1);
        assertEquals(subTaskOne, subTaskTwo, "Объекты не равны");
    }

    @Test
    public void subTaskCannotBeEpic() {
        Epic epic = new Epic("Test Epic", "Description", Status.NEW, null, null);
        Epic createdEpic = manager.createEpic(epic);


        SubTask subTask = new SubTask("SubTask", "Description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2025, 11, 29, 15, 0), createdEpic.getId());

        manager.createSubTask(subTask);


        assertFalse(manager.getAllSubTasks().isEmpty(), "Подзадача должна быть добавлена");
        SubTask retrievedSubTask = manager.getAllSubTasks().get(0);
        assertEquals(TypeTask.SUBTASK, retrievedSubTask.getType(), "Тип задачи должен быть SUBTASK");
        assertNotEquals(TypeTask.EPIC, retrievedSubTask.getType(), "Тип задачи не должен быть EPIC");
    }
}