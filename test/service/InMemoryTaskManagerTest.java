package service;

import model.*;
import utils.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;
    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldRemoveTaskFromHistoryAfterDeletion() {
        Task task = new Task(
                "Task 1",
                "Description",
                Status.NEW,
                Duration.ofMinutes(60),
                LocalDateTime.now()
        );
        taskManager.createTask(task);
        taskManager.getTaskByID(task.getId());

        taskManager.deleteTask(task.getId());

        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления задачи.");
    }

    @Test
    void shouldRemoveSubtasksFromEpicAfterDeletion() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);

        SubTask subtask = new SubTask(
                "Subtask 1",
                "Description",
                Duration.ofMinutes(90),
                LocalDateTime.now().plusDays(1),
                epic.getId()
        );

        taskManager.createSubtask(subtask,epic.getId());
        taskManager.deleteSubtask(subtask.getId());


        Epic updatedEpic = taskManager.getEpicByID(epic.getId());
        assertTrue(updatedEpic.getSubtaskList().isEmpty(), "Список подзадач в эпике должен быть пустым после удаления подзадачи.");
    }

    @Test
    void shouldNotRetainOldIdsAfterSubtaskDeletion() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);

        SubTask subtask = new SubTask(
                "Subtask",
                "Description",
                Status.NEW,
                Duration.ofMinutes(45),
                LocalDateTime.now().plusHours(2),
                epic.getId()
        );
        taskManager.createSubtask(subtask,epic.getId());
        taskManager.deleteSubtask(subtask.getId());

        assertFalse(taskManager.getSubtasks().contains(subtask), "Удалённая подзадача не должна присутствовать в списке всех подзадач.");
        assertFalse(epic.getSubtaskList().contains(subtask.getId()), "Идентификатор удалённой подзадачи не должен оставаться в эпике.");
    }

    @Test
    void shouldUpdateEpicStatusCorrectlyWhenSubtasksChange() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);

        SubTask subtask1 = new SubTask(
                "Subtask 1",
                "Description",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.now().plusHours(3),
                epic.getId()
        );
        SubTask subtask2 = new SubTask(
                "Subtask 2",
                "Description",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.now().plusHours(4),
                epic.getId()
        );
        taskManager.createSubtask(subtask1,epic.getId());
        taskManager.createSubtask(subtask2,epic.getId());

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicByID(epic.getId()).getStatus(), "Статус эпика должен быть IN_PROGRESS после выполнения одной из подзадач.");

        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);

        assertEquals(Status.DONE, taskManager.getEpicByID(epic.getId()).getStatus(), "Статус эпика должен быть DONE после выполнения всех подзадач.");
    }
}