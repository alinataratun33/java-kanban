import manager.FileBackedTaskManager;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import tasks.Epic;

import java.io.File;


public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        File file = new File("C:\\Users\\MyPC\\Desktop\\tasks.csv");

        FileBackedTaskManager manager1 = new FileBackedTaskManager(file);
        Task task = manager1.createTask(new Task("Задача1", "Описание", Status.NEW));
        Epic epic = manager1.createEpic(new Epic("Эпик1", "Описание", Status.NEW));
        SubTask subTask = manager1.createSubTask(new SubTask("Подзадача1", "Описание",
                Status.NEW, 2));
        
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);

        print(manager2);
    }

    private static void print(FileBackedTaskManager manager) {
        System.out.println("Задачи: " + manager.getAllTasks().size());
        System.out.println("Эпики: " + manager.getAllEpics().size());
        System.out.println("Подзадачи: " + manager.getAllSubTasks().size());

        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }
        for (SubTask subTask : manager.getAllSubTasks()) {
            System.out.println(subTask);
        }
    }
}



