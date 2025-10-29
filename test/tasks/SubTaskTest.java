package tasks;

import manager.Managers;
import manager.TaskManager;
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
        SubTask subTask = new SubTask("SubTask", "Description", Status.NEW,Duration.ofMinutes(30),
                LocalDateTime.of(2025, 11,29, 15, 0), 1);
        manager.createSubTask(subTask);
        assertTrue(manager.getAllSubTasks().isEmpty(), "Подзадача добавлена как эпик");
    }
}