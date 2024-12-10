package service;

import model.*;
import utils.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    TaskManager taskManager;

    Task task;
    Epic epic;
    SubTask subtask1;
    SubTask subtask2;


    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();

        task = new Task("Task 1", "Task 1 description", Status.NEW);
        taskManager.createTask(task);
        epic = new Epic("Epic 1", "Description of Epic 1");
        taskManager.createEpic(epic);
        subtask1 = new SubTask("Subtask 1", "Description subtask 1", epic.getId());
        subtask2 = new SubTask("Subtask 2", "Description subtask 2", epic.getId());
        taskManager.createSubtask(subtask1,epic.getId());
        taskManager.createSubtask(subtask2,epic.getId());
    }

    @Test
    void shouldCreateAndAddAllTypesOfTasks(){
        assertEquals(task, taskManager.getTaskByID(task.getId()));
        assertEquals(epic, taskManager.getEpicByID(epic.getId()));
        assertEquals(subtask1, taskManager.getSubtaskByID(subtask1.getId()));
        assertEquals(subtask2, taskManager.getSubtaskByID(subtask2.getId()));
    }

    @Test
    void shouldReturnNotEqualsByComparingUpdatedTaskWithTaskFromHistory(){
        Task calledTask = taskManager.getTaskByID(task.getId());
        List<Task> tasksFromHistory = taskManager.getHistory();
        System.out.println(tasksFromHistory);
        assertEquals(calledTask, tasksFromHistory.get(0));

        task.setName("Updated name for task");
        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTaskByID(task.getId());

        assertNotEquals(updatedTask, tasksFromHistory);
    }

    @Test
    void shouldRemoveTaskFromHistoryAfterDeletion() {
        taskManager.getTaskByID(task.getId());
        taskManager.deleteTask(task.getId());

        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления задачи.");
    }

    @Test
    void shouldNotRetainOldIdsAfterSubtaskDeletion() {
        taskManager.deleteSubtask(subtask1.getId());

        assertFalse(taskManager.getSubtasks().contains(subtask1), "Удалённая подзадача не должна присутствовать в списке всех подзадач.");
        assertFalse(epic.getSubtaskList().contains(subtask1.getId()), "Идентификатор удалённой подзадачи не должен оставаться в эпике.");
    }

    @Test
    void shouldRemoveSubtasksFromEpicAfterDeletion() {
        taskManager.deleteSubtask(subtask1.getId());
        taskManager.deleteSubtask(subtask2.getId());

        Epic updatedEpic = taskManager.getEpicByID(epic.getId());
        assertTrue(updatedEpic.getSubtaskList().isEmpty(), "Список подзадач в эпике должен быть пустым после удаления подзадачи.");
    }
}