import manager.FileBackedTaskManager;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import tasks.Epic;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        File file = new File("C:\\Users\\MyPC\\Desktop\\tasks.csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task1 = manager.createTask(new Task("Задача", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 11, 25, 10, 0)));


        Epic epic = manager.createEpic(new Epic("Эпик с подзадачами", "Описание", Status.NEW,
                null, null));

        SubTask subTask1 = manager.createSubTask(new SubTask("Подзадача ранняя", "Описание",
                Status.DONE,
                Duration.ofMinutes(25), LocalDateTime.of(2025, 11, 25, 7, 0),
                epic.getId()));
        SubTask subTask2 = manager.createSubTask(new SubTask("Подзадача поздняя", "Описание",
                Status.DONE,
                Duration.ofMinutes(35), LocalDateTime.of(2025, 11, 25, 15, 0),
                epic.getId()));

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        System.out.println("===Приоритетные задачи===");
        for (Task taskss : prioritizedTasks) {
            System.out.println(taskss.getName() + " - " + taskss.getStartTime().toLocalTime());
        }
        print(manager2);
    }

    private static void print(FileBackedTaskManager manager) {
        System.out.println("Задачи: " + manager.getAllTasks().size());
        System.out.println("Эпики: " + manager.getAllEpics().size());
        System.out.println("Подзадачи: " + manager.getAllSubTasks().size());

        for (Task task : manager.getAllTasks()) {

            System.out.println(task);
            System.out.println("Задача будет закончена: " + task.getEndTime());
        }
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
            System.out.println("Эпик будет закончен: " + epic.getEndTime());
        }
        for (SubTask subTask : manager.getAllSubTasks()) {
            System.out.println(subTask);
            System.out.println("Подзадача будет закончена: " + subTask.getEndTime());
        }
    }
}



