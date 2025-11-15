package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager) {
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
                System.out.println("Ошибка");
        }
    }

    private void handleGetRequest(String path, HttpExchange exchange) throws IOException {
        if (path.equals("/epics")) {
            List<Epic> epics = taskManager.getAllEpics();
            String epicsJson = gson.toJson(epics);
            sendText(exchange, epicsJson, 200);
        } else if (path.matches("/epics/\\d+")) {
            String[] pathParts = path.split("/");
            int id = Integer.parseInt(pathParts[2]);
            Epic epic = taskManager.getEpicById(id);

            String epicJson = gson.toJson(epic);
            sendText(exchange, epicJson, 200);

        } else if (path.matches("/epics/\\d+/subtasks")) {
            String[] pathParts = path.split("/");
            int id = Integer.parseInt(pathParts[2]);
            List<SubTask> subTasks = taskManager.getSubTasksForEpic(id);
            String subTaskJson = gson.toJson(subTasks);
            sendText(exchange, subTaskJson, 200);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePostRequest(String path, HttpExchange exchange) throws IOException {
        if (path.equals("/epics")) {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            try {
                Epic epic = gson.fromJson(body, Epic.class);
                if (epic == null) {
                    sendNotFound(exchange);
                }
                if (epic.getId() != 0) {
                    taskManager.updateEpic(epic);
                    String responseJson = gson.toJson(epic);
                    sendText(exchange, responseJson, 200);
                } else {
                    Epic createdEpic = taskManager.createEpic(epic);
                    String responseJson = gson.toJson(createdEpic);
                    sendText(exchange, responseJson, 201);
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void handleDeleteRequest(String path, HttpExchange exchange) throws IOException {
        if (path.matches("/epics/\\d+")) {
            String[] pathParts = path.split("/");
            int id = Integer.parseInt(pathParts[2]);
            taskManager.removeEpicById(id);
            sendText(exchange, "Эпик с ID " + id + " удален", 200);
        } else if (path.equals("/epics")) {
            taskManager.removeAllEpics();
            sendText(exchange, "Все эпики удалены", 200);
        }
    }
}

