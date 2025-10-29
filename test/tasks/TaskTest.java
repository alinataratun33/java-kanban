package tasks;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void shouldReturnEqualsTwoTasks() {
        Task taskOne = new Task("tasks", "Description", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.of(2025, 11,29, 15, 0));
        taskOne.setId(1);
        Task taskTwo = new Task("tasks", "Description", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.of(2025, 11,29, 15, 0));
        taskTwo.setId(1);
        assertEquals(taskOne, taskTwo, "Объекты не равны");
    }

}