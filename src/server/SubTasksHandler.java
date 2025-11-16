package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.ManagerConflictException;
import manager.NotFoundException;
import manager.TaskManager;
import tasks.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class SubTasksHandler extends BaseHttpHandler {
    private final Gson gson;

    public SubTasksHandler(TaskManager taskManager) {
        super(taskManager);
        this.gson = createGsonWithAdapters();
    }

    private Gson createGsonWithAdapters() {
        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).registerTypeAdapter(Duration.class, new DurationAdapter()).setPrettyPrinting().create();
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
                sendBadRequest(exchange, "Метод неккоректен");
        }
    }


    private void handleGetRequest(String path, HttpExchange exchange) throws IOException {
        try {
            if (path.equals("/subtasks")) {
                List<SubTask> subtasks = taskManager.getAllSubTasks();
                String subtasksJson = gson.toJson(subtasks);
                sendText(exchange, subtasksJson, 200);
            } else if (path.matches("/subtasks/\\d+")) {
                Integer id = parseIdFromPath(path, exchange);
                if (id == null) return;

                SubTask subTask = taskManager.getSubTaskById(id);
                String subtaskJson = gson.toJson(subTask);
                sendText(exchange, subtaskJson, 200);
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


            if (path.equals("/subtasks")) {

                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                SubTask subTask = gson.fromJson(body, SubTask.class);

                if (subTask.getId() != 0) {
                    taskManager.updateSubTask(subTask);
                    String responseJson = gson.toJson(subTask);
                    sendText(exchange, responseJson, 200);
                } else {
                    SubTask createdSubTask = taskManager.createSubTask(subTask);
                    String responseJson = gson.toJson(createdSubTask);
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
            if (path.matches("/subtasks/\\d+")) {
                Integer id = parseIdFromPath(path, exchange);
                if (id == null) return;

                taskManager.removeSubTaskById(id);
                sendText(exchange, "Подзадача с ID " + id + " удалена", 200);
            } else if (path.equals("/subtasks")) {
                taskManager.removeAllSubTasks();
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



