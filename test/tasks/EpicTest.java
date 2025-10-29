package tasks;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {

    TaskManager manager;

    @BeforeEach
    public void setUp() {
        manager = Managers.getDefault();

    }


    @Test
    public void shouldReturnEqualsTwoEpics() {
        Epic epicOne = new Epic("Epic", "Description", Status.NEW, null, null);
        epicOne.setId(1);
        Epic epicTwo = new Epic("Epic", "Description", Status.NEW, null, null);
        epicTwo.setId(1);
        assertEquals(epicOne, epicTwo, "Объекты не равны");
    }

    @Test
    public void testEpicCannotBeAddedAsASubTask() {
        Epic epic = new Epic("Epic", "Description", Status.NEW, null, null);
        epic.setId(1);
        epic.addSubTaskId(1);
        assertTrue(epic.getSubTaskIds().isEmpty(), "Эпик был добален как подзадача");
    }

    @Test
    public void testStatusEpic() {
        Epic epicWithStatusNew = manager.createEpic(new Epic("Epic", "Description", Status.NEW,
                null, null));
        SubTask subTaskWithStatusNewOne = manager.createSubTask(new SubTask("SubTask1", "Description",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2025, 10, 27, 10, 0), epicWithStatusNew.getId()));
        SubTask subTaskWithStatusNewSecond = manager.createSubTask(new SubTask("SubTask2",
                "Description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2025, 10, 27, 11, 0), epicWithStatusNew.getId()));

        Epic epicWithStatusDone = manager.createEpic(new Epic("Epic", "Description", Status.NEW,
                null, null));
        SubTask subTaskWithStatusDoneOne = manager.createSubTask(new SubTask("SubTask3", "Description",
                Status.DONE, Duration.ofMinutes(30),
                LocalDateTime.of(2025, 10, 28, 10, 0), epicWithStatusDone.getId())); // 10:00
        SubTask subTaskWithStatusDoneSecond = manager.createSubTask(new SubTask("SubTask4",
                "Description", Status.DONE, Duration.ofMinutes(30),
                LocalDateTime.of(2025, 10, 28, 11, 0), epicWithStatusDone.getId())); // 11:00

        Epic epicWithStatusInProgress = manager.createEpic(new Epic("Epic", "Description", Status.NEW,
                null, null));
        SubTask subTaskWithStatusNew = manager.createSubTask(new SubTask("SubTask5",
                "Description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2025, 10, 29, 10, 0), epicWithStatusInProgress.getId())); // 10:00
        SubTask subTaskWithStatusDone = manager.createSubTask(new SubTask("SubTask6",
                "Description", Status.DONE, Duration.ofMinutes(30),
                LocalDateTime.of(2025, 10, 29, 11, 0), epicWithStatusInProgress.getId())); // 11:00

        Epic epicWithStatusInProgressTwo = manager.createEpic(new Epic("Epic", "Description", Status.NEW,
                null, null));
        SubTask subTaskWithStatusInProgressOne = manager.createSubTask(new SubTask("SubTask7",
                "Description", Status.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2025, 10, 30, 10, 0), epicWithStatusInProgressTwo.getId())); // 10:00
        SubTask subTaskWithStatusInProgressSecond = manager.createSubTask(new SubTask("SubTask8",
                "Description", Status.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2025, 10, 30, 11, 0), epicWithStatusInProgressTwo.getId())); // 11:00


        assertEquals(Status.NEW, epicWithStatusNew.getStatus(), "У эпика должен быть статус NEW");
        assertEquals(Status.DONE, epicWithStatusDone.getStatus(), "У эпика должен быть статус DONE");
        assertEquals(Status.IN_PROGRESS, epicWithStatusInProgress.getStatus(),
                "У эпика должен быть статус IN_PROGRESS");
        assertEquals(Status.IN_PROGRESS, epicWithStatusInProgressTwo.getStatus(),
                "У эпика должен быть статус IN_PROGRESS");
    }
}
