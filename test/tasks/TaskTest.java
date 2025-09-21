package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void shouldReturnEqualsTwoTasks() {
        Task taskOne = new Task("tasks", "Description", Status.NEW);
        taskOne.setId(1);
        Task taskTwo = new Task("tasks", "Description", Status.NEW);
        taskTwo.setId(1);
        assertEquals(taskOne, taskTwo, "Объекты не равны");
    }

}