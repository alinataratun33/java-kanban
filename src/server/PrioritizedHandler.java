package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager) {
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

        if (path.equals("/prioritized") && method.equals("GET")) {
            List<Task> prioritized = taskManager.getPrioritizedTasks();
            String prioritizedJson = gson.toJson(prioritized);
            sendText(exchange, prioritizedJson, 200);
        }
    }
}
