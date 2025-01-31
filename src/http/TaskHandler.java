package http;

import com.sun.net.httpserver.HttpExchange;
import service.*;
import model.*;
import java.io.IOException;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("/tasks".equals(path)) {
            sendText(exchange, gson.toJson(taskManager.getTasks()), 200);
        } else if (path.matches("/tasks/\\d+")) {
            int taskId = Integer.parseInt(path.split("/")[2]);
            Task task = taskManager.getTaskByID(taskId);
            if (task != null) {
                sendText(exchange, gson.toJson(task), 200);
            } else {
                sendNotFound(exchange, "Задача не найдена");
            }
        } else {
            sendNotFound(exchange, "Эндпоинт не найден");
        }
    }

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("/tasks".equals(path)) {
            String body = readRequestBody(exchange);
            Task task = gson.fromJson(body, Task.class);
            if (task.getId() == 0) {
                taskManager.createTask(task);
                sendText(exchange, gson.toJson(task), 201);
            } else {
                Task existing = taskManager.getTaskByID(task.getId());
                if (existing == null) {
                    sendNotFound(exchange, "Задача не найдена");
                    return;
                }
                taskManager.updateTask(task);
                sendText(exchange, gson.toJson(task), 200);
            }
        } else {
            sendNotFound(exchange, "Эндпоинт не найден");
        }
    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.matches("/tasks/\\d+")) {
            int taskId = Integer.parseInt(path.split("/")[2]);
            Task task = taskManager.getTaskByID(taskId);
            if (task == null) {
                sendNotFound(exchange, "Задача не найдена");
            } else {
                taskManager.deleteTask(taskId);
                sendText(exchange, "{}", 200);
            }
        } else {
            sendNotFound(exchange, "Эндпоинт не найден");
        }
    }
}
