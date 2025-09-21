import Manager.HistoryManager;
import Manager.Managers;
import Manager.TaskManager;
import Tasks.Status;
import Tasks.SubTask;
import Tasks.Task;
import Tasks.Epic;


public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");


        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task taskForUpdate = manager.createTask(new Task("Задача1", "Описание", Status.NEW));
        Task taskForRemove = manager.createTask(new Task("Задача2", "Описание", Status.NEW));
        Task task3 = manager.createTask(new Task("Задача3", "Описание", Status.NEW));

        Epic epicForUpdate = manager.createEpic(new Epic("Эпик1", "Описание", Status.NEW));
        Epic epicForRemove = manager.createEpic(new Epic("Эпик2", "Описание", Status.NEW));
        Epic epic3 = manager.createEpic(new Epic("Эпик3", "Описание", Status.NEW));

        SubTask subTaskForUpdate = manager.createSubTask(new SubTask("Подзадача1", "Описание",
                Status.NEW, 5));
        SubTask subTaskForRemove = manager.createSubTask(new SubTask("Подзадача2", "Описание",
                Status.NEW, 6));
        SubTask subTask3 = manager.createSubTask(new SubTask("Подзадача3", "Описание",
                Status.NEW, 6));


        manager.getEpicById(5);
        manager.getTaskById(1);
        manager.getEpicById(6);
        manager.getEpicById(6);
        manager.getTaskById(2);
        manager.getSubTaskById(9);
        manager.getEpicById(5);


        updateTask(manager, taskForUpdate);
        updateSubTask(manager, subTaskForUpdate);
        updateEpic(manager, epicForUpdate);

        removeTaskById(manager);

        printAllTasks(manager, historyManager);
        removeAllTasks(manager);
    }

    private static void removeTaskById(TaskManager manager) {
        manager.removeTaskById(2);
        manager.removeEpicById(6);
        manager.removeSubTaskById(8);
    }

    private static void removeAllTasks(TaskManager manager) {
        manager.removeAllTasks();
        manager.removeAllEpics();
        manager.removeAllSubTasks();
        if (manager.getAllTasks().isEmpty() && manager.getAllEpics().isEmpty() && manager.getAllSubTasks().isEmpty()) {
            System.out.println("Все задачи удалены!");
        }
    }

    private static void updateEpic(TaskManager manager, Epic epic) {
        Epic updateEpic = new Epic("Измененный эпик", "Описание изменено", Status.IN_PROGRESS);
        updateEpic.setId(epic.getId());
        manager.updateEpic(updateEpic);
    }

    private static void updateSubTask(TaskManager manager, SubTask subTask) {
        SubTask updateSubTask = new SubTask("Измененная подзадача", "Описание изменено",
                Status.IN_PROGRESS, 4);
        updateSubTask.setId(subTask.getId());
        manager.updateSubTask(updateSubTask);
    }

    private static void updateTask(TaskManager manager, Task task) {
        Task updateTask = new Task("Измененная задача", "Описание изменено", Status.IN_PROGRESS);
        updateTask.setId(task.getId());
        manager.updateTask(updateTask);
    }

    private static void printAllTasks(TaskManager manager, HistoryManager historyManager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);
            for (Task subtask : manager.getSubTasksForEpic(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }

        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("\nИстория просмотров:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
