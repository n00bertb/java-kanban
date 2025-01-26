package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    public void shouldCalculateEpicStatusWhenAllSubtasksNew() {
        Epic epic = new Epic("Epic 1", "Description");
        taskManager.createEpic(epic);
        LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 1, 10, 0);
        SubTask subtask1 = new SubTask("Subtask 1", "Description", Status.NEW,
                Duration.ofMinutes(30), fixedTime, epic.getId());
        SubTask subtask2 = new SubTask("Subtask 2", "Description", Status.NEW,
                Duration.ofMinutes(30), fixedTime.plusMinutes(40), epic.getId());
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW, если все подзадачи NEW.");
        assertEquals(Duration.ofMinutes(60), epic.getDuration(), "Длительность эпика должна быть суммой длительностей подзадач.");
    }

    @Test
    public void shouldCalculateEpicStatusWhenAllSubtasksDone() {
        Epic epic = new Epic("Epic 1", "Description");
        taskManager.createEpic(epic);
        LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 1, 10, 0);
        SubTask subtask1 = new SubTask("Subtask 1", "Description", Status.DONE,
                Duration.ofMinutes(30), fixedTime, epic.getId());
        SubTask subtask2 = new SubTask("Subtask 2", "Description", Status.DONE,
                Duration.ofMinutes(30), fixedTime.plusMinutes(40), epic.getId());
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());
        assertEquals(Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE, если все подзадачи DONE.");
        assertEquals(Duration.ofMinutes(60), epic.getDuration(), "Длительность эпика должна быть суммой длительностей подзадач.");
    }

    @Test
    public void shouldNotAllowOverlappingTasks() {
        LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 1, 10, 0);
        Task task1 = new Task("Task 1", "Description", Status.NEW,
                Duration.ofMinutes(30), fixedTime);
        Task task2 = new Task("Task 2", "Description", Status.NEW,
                Duration.ofMinutes(30), fixedTime.plusMinutes(15));
        taskManager.createTask(task1);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(task2));
        assertEquals("Задача пересекается с другой задачей по времени выполнения.", exception.getMessage());
    }

    @Test
    public void shouldAllowNonOverlappingTasks() {
        LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 1, 10, 0);
        Task task1 = new Task("Task 1", "Description", Status.NEW,
                Duration.ofMinutes(30), fixedTime);
        Task task2 = new Task("Task 2", "Description", Status.NEW,
                Duration.ofMinutes(30), fixedTime.plusMinutes(40));
        taskManager.createTask(task1);
        assertDoesNotThrow(() -> taskManager.createTask(task2), "Не должно быть исключений для непересекающихся задач.");
    }

    @Test
    public void shouldCalculateEpicStatusWithoutSubtasks() {
        Epic epic = new Epic("Epic 1", "Description");
        taskManager.createEpic(epic);
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика без подзадач должен быть NEW.");
    }

    @Test
    public void shouldRemoveSubtaskFromEpic() {
        Epic epic = new Epic("Epic 1", "Description");
        taskManager.createEpic(epic);
        SubTask subtask1 = new SubTask("Subtask 1", "Description", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 10, 0), epic.getId());
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.deleteSubtask(subtask1.getId());
        assertTrue(epic.getSubtaskList().isEmpty(), "После удаления подзадачи она должна быть удалена из эпика.");
    }

    @Test
    public void shouldPrioritizeTasksCorrectly() {
        LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 1, 10, 0);
        Task task1 = new Task("Task 1", "Description", Status.NEW,
                Duration.ofMinutes(30), fixedTime);
        Task task2 = new Task("Task 2", "Description", Status.NEW,
                Duration.ofMinutes(30), fixedTime.plusMinutes(40));
        taskManager.createTask(task2);
        taskManager.createTask(task1);
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(task1, prioritizedTasks.get(0), "Первая задача должна быть Task 1.");
        assertEquals(task2, prioritizedTasks.get(1), "Вторая задача должна быть Task 2.");
    }
}
