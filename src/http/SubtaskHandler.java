package http;

import com.sun.net.httpserver.HttpExchange;
import service.*;
import model.*;
import java.io.IOException;
public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("/subtasks".equals(path)) {
            sendText(exchange, gson.toJson(taskManager.getSubtasks()), 200);
        } else if (path.matches("/subtasks/\\d+")) {
            int subtaskId = Integer.parseInt(path.split("/")[2]);
            SubTask subtask = taskManager.getSubtaskByID(subtaskId);
            if (subtask == null) {
                sendNotFound(exchange, "Подзадача не найдена");
            } else {
                sendText(exchange, gson.toJson(subtask), 200);
            }
        } else {
            sendNotFound(exchange, "Эндпоинт не найден");
        }
    }

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("/subtasks".equals(path)) {
            String body = readRequestBody(exchange);
            SubTask subtask = gson.fromJson(body, SubTask.class);
            if (subtask.getId() == 0) {
                taskManager.createSubtask(subtask, subtask.getEpicID());
                sendText(exchange, gson.toJson(subtask), 201);
            } else {
                SubTask existing = taskManager.getSubtaskByID(subtask.getId());
                if (existing == null) {
                    sendNotFound(exchange, "Subtask not found");
                    return;
                }
                taskManager.updateSubtask(subtask);
                sendText(exchange, gson.toJson(subtask), 200);
            }
        } else {
            sendNotFound(exchange, "Endpoint not found");
        }
    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.matches("/subtasks/\\d+")) {
            int subtaskId = Integer.parseInt(path.split("/")[2]);
            SubTask subtask = taskManager.getSubtaskByID(subtaskId);
            if (subtask == null) {
                sendNotFound(exchange, "Subtask not found");
            } else {
                taskManager.deleteSubtask(subtaskId);
                sendText(exchange, "{}", 200);
            }
        } else {
            sendNotFound(exchange, "Endpoint not found");
        }
    }
}
