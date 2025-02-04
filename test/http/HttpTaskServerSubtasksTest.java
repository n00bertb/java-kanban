package http;

import org.junit.jupiter.api.Test;
import model.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerSubtasksTest extends BaseHttpTest {
    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic Description");
        manager.createEpic(epic);
        SubTask subtask = new SubTask("Test Subtask", "Subtask Description",
                Duration.ofMinutes(30), LocalDateTime.now(), epic.getId());
        String subtaskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Некорректный статус ответа при добавлении подзадачи");
        assertEquals(1, manager.getSubtasks().size(), "Подзадача не добавлена в менеджер");
    }
}