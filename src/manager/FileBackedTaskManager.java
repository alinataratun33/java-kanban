package manager;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager {
    final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask createdSubTask = super.createSubTask(subTask);
        save();
        return createdSubTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }

    protected void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,duration,dateTime, epic" + "\n");
            for (Task task : getAllTasks()) {
                fileWriter.write(task.toString() + "\n");
            }
            for (Epic epic : getAllEpics()) {
                fileWriter.write(epic.toString() + "\n");
            }
            for (SubTask subTask : getAllSubTasks()) {
                fileWriter.write(subTask.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить информацию в файл");
        }
    }

    private static Task fromString(String value) {
        String[] split = value.split(",");

        int id = Integer.parseInt(split[0]);
        String type = split[1];
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        int duration = Integer.parseInt(split[5]);
        String startTimeStr = split[6];

        LocalDateTime startTime = null;
        if (startTimeStr != null && !startTimeStr.isEmpty() && !startTimeStr.equals("null")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                startTime = LocalDateTime.parse(startTimeStr, formatter);
        }

        switch (type) {
            case "TASK":
                Task task = new Task(name, description, status, Duration.ofMinutes(duration), startTime);
                task.setId(id);
                return task;
            case "EPIC":
                Epic epic = new Epic(name, description, status, Duration.ofMinutes(duration), startTime);
                epic.setId(id);
                return epic;
            case "SUBTASK":
                int epicId = Integer.parseInt(split[7]);
                SubTask subTask = new SubTask(name, description, status, Duration.ofMinutes(duration), startTime,
                        epicId);
                subTask.setId(id);
                return subTask;

            default:
                return null;
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        int maxId = 0;

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        if (!file.exists()) {
            return manager;
        }
        try {
            String tasksInFile = Files.readString(file.toPath());
            String[] lines = tasksInFile.split("\n");
            for (int i = 1; i < lines.length; i++) {
                Task task = fromString(lines[i]);
                if (task != null) {
                    manager.addTaskFromFile(task);
                    if (task.getId() > maxId) {
                        maxId = task.getId();
                    }
                }
            }
            manager.setCount(maxId);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось получить информацию из файла");
        }
        return manager;
    }
}
