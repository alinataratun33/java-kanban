package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTest {

    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private final HttpClient client = HttpClient.newHttpClient();

    public HttpServerTest() throws IOException {
        this.manager = new InMemoryTaskManager();
        this.taskServer = new HttpTaskServer(manager, 8080);
        this.gson = createGson();
    }

    private Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .create();
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.removeAllSubTasks();
        manager.removeAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("TestTask", "Description",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());


        String taskJson = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        assertEquals(201, response.statusCode(), "Некорректный статус код при создании задачи");

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("TestTask", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {

        Task task = new Task("TestTask", "Description", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        Task createdTask = manager.createTask(task);
        int taskId = createdTask.getId();


        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный статус код при получении задачи");


        Task responseTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(responseTask, "Задача не вернулась в ответе");
        assertEquals(taskId, responseTask.getId(), "Некорректный ID задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("OriginalTask", "OriginalDescription", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        Task createdTask = manager.createTask(task);

        Task updatedTask = new Task("UpdatedTask", "UpdatedDescription", Status.IN_PROGRESS,
                Duration.ofMinutes(20), LocalDateTime.now().plusHours(1));
        updatedTask.setId(createdTask.getId());

        String updatedTaskJson = gson.toJson(updatedTask);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Некорректный статус код при обновлении задачи");


        Task taskFromManager = manager.getTaskById(createdTask.getId());
        assertEquals("UpdatedTask", taskFromManager.getName(), "Имя задачи не обновилось");
        assertEquals(Status.IN_PROGRESS, taskFromManager.getStatus(), "Статус задачи не обновился");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {

        Task task = new Task("TaskDelete", "Description", Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        Task createdTask = manager.createTask(task);
        int taskId = createdTask.getId();


        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный статус код при удалении задачи");


        assertThrows(Exception.class, () -> manager.getTaskById(taskId),
                "Задача должна бросать исключение при попытке получить удаленную задачу");
    }
}