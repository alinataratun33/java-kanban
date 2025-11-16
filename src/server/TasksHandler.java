package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.ManagerConflictException;
import manager.NotFoundException;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    private final Gson gson;

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
        this.gson = createGsonWithAdapters();
    }

    private Gson createGsonWithAdapters() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGetRequest(path, exchange);
                break;
            case "POST":
                handlePostRequest(path, exchange);
                break;
            case "DELETE":
                handleDeleteRequest(path, exchange);
                break;
            default:
                sendBadRequest(exchange, "Метод некорректен");
        }
    }

    private void handleGetRequest(String path, HttpExchange exchange) throws IOException {
        try {
            if (path.equals("/tasks")) {
                List<Task> tasks = taskManager.getAllTasks();
                String tasksJson = gson.toJson(tasks);
                sendText(exchange, tasksJson, 200);
            } else if (path.matches("/tasks/\\d+")) {
                Integer id = parseIdFromPath(path, exchange);
                if (id == null) return;

                Task task = taskManager.getTaskById(id);
                String taskJson = gson.toJson(task);
                sendText(exchange, taskJson, 200);
            } else {
                sendBadRequest(exchange, "Некорректный путь");
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendBadRequest(exchange, "Ошибка при обработке запроса");
        }
    }


    private void handlePostRequest(String path, HttpExchange exchange) throws IOException {
        try {
            if (path.equals("/tasks")) {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);


                Task task = gson.fromJson(body, Task.class);


                if (task.getId() != 0) {
                    taskManager.updateTask(task);
                    String responseJson = gson.toJson(task);
                    sendText(exchange, responseJson, 200);
                } else {
                    Task createdTask = taskManager.createTask(task);
                    String responseJson = gson.toJson(createdTask);
                    sendText(exchange, responseJson, 201);
                }
            }
        } catch (ManagerConflictException e) {
            sendHasInteractions(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }


    private void handleDeleteRequest(String path, HttpExchange exchange) throws IOException {
        try {
            if (path.matches("/tasks/\\d+")) {
                Integer id = parseIdFromPath(path, exchange);
                if (id == null) return;

                taskManager.removeTaskById(id);
                sendText(exchange, "Задача с ID " + id + " удалена", 200);
            } else if (path.equals("/tasks")) {
                taskManager.removeAllTasks();
                sendText(exchange, "Все задачи удалены", 200);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendBadRequest(exchange, "Ошибка при обработке запроса");
        }
    }

    private Integer parseIdFromPath(String path, HttpExchange exchange) throws IOException {
        try {
            String[] pathParts = path.split("/");
            return Integer.parseInt(pathParts[2]);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Некорректный формат ID");
            return null;
        }
    }
}
