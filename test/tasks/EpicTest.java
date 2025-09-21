package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {

    @Test
    public void shouldReturnEqualsTwoEpics() {
        Epic epicOne = new Epic("Task.Task.Epic", "Description", Status.NEW);
        epicOne.setId(1);
        Epic epicTwo = new Epic("Task.Task.Epic", "Description", Status.NEW);
        epicTwo.setId(1);
        assertEquals(epicOne, epicTwo, "Объекты не равны");
    }

    @Test
    public void testEpicCannotBeAddedAsASubTask() {
        Epic epic = new Epic("Task.Task.Epic", "Description", Status.NEW);
        epic.setId(1);
        epic.addSubTaskId(1);
        assertTrue(epic.getSubTaskIds().isEmpty(), "Эпик был добален как подзадача");
    }
}