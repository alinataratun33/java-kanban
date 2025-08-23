import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void shouldReturnEqualsTwoTasks() {
        Task taskOne = new Task("Task", "Description", Status.NEW);
        taskOne.setId(1);
        Task taskTwo = new Task("Task", "Description", Status.NEW);
        taskTwo.setId(1);
        assertEquals(taskOne, taskTwo, "Объекты не равны");
    }

}