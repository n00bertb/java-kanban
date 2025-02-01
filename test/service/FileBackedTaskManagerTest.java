package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test", ".csv");
    }

    @Test
    void shouldSaveAndLoadEmptyFile() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getTasks().isEmpty(), "Список задач должен быть пустым после загрузки из пустого файла.");
        assertTrue(loadedManager.getEpics().isEmpty(), "Список эпиков должен быть пустым после загрузки из пустого файла.");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Список подзадач должен быть пустым после загрузки из пустого файла.");
    }

    @Test
    void shouldSaveAndLoadMultipleTasks() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Task 1", "Description 1", Duration.ofMinutes(120),
                LocalDateTime.now());
        Epic epic1 = new Epic("Epic 1", "Description 2");
        SubTask subtask1 = new SubTask("Subtask 1", "Description 3",Duration.ofMinutes(60),
                LocalDateTime.now().plusHours(6), 2);

        manager.createTask(task1);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);

        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> tasks = loadedManager.getTasks();
        List<Epic> epics = loadedManager.getEpics();
        List<SubTask> subtasks = loadedManager.getSubtasks();

        assertEquals(1, tasks.size(), "Должна быть загружена одна задача.");
        assertEquals(1, epics.size(), "Должен быть загружен один эпик.");
        assertEquals(1, subtasks.size(), "Должна быть загружена одна подзадача.");

        assertEquals(task1, tasks.get(0), "Загруженная задача должна совпадать с сохранённой.");
        assertEquals(epic1, epics.get(0), "Загруженный эпик должен совпадать с сохранённым.");
        assertEquals(subtask1, subtasks.get(0), "Загруженная подзадача должна совпадать с сохранённой.");
    }

    @Test
    void shouldLoadManagerFromFileWithMultipleTasks() throws IOException {
        // Создаем и записываем несколько задач в файл
        String csvContent = String.join("\n",
                "id,type,name,status,description,duration,startTime,epic",
                "1,TASK,Task 1,NEW,Description of task 1,30,2023-01-01T10:00,",
                "2,TASK,Task 2,IN_PROGRESS,Description of task 2,45,2023-01-01T12:30,",
                "3,EPIC,Epic 1,IN_PROGRESS,Description of epic 1,,,,",
                "4,SUBTASK,Subtask 1,NEW,Description of subtask 1,20,2023-01-01T11:00,3",
                "5,SUBTASK,Subtask 2,DONE,Description of subtask 2,25,2023-01-01T11:30,3"
        );

        Files.writeString(tempFile.toPath(), csvContent);

        // Загружаем менеджер из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        Task loadedTask1 = loadedManager.getTaskByID(1);
        Task loadedTask2 = loadedManager.getTaskByID(2);
        Epic loadedEpic = loadedManager.getEpicByID(3);
        SubTask loadedSubtask1 = loadedManager.getSubtaskByID(4);
        SubTask loadedSubtask2 = loadedManager.getSubtaskByID(5);
        // Проверяем, что задачи, эпики и подзадачи были загружены
        assertNotNull(loadedTask1, "Task 1 должен быть загружен.");
        assertNotNull(loadedTask2, "Task 2 должен быть загружен.");
        assertNotNull(loadedEpic, "Epic должен быть загружен.");
        assertNotNull(loadedSubtask1, "Subtask 1 должен быть загружен.");
        assertNotNull(loadedSubtask2, "Subtask 2 должен быть загружен.");
        // Проверяем, что значения совпадают с ожидаемыми
        assertEquals("Task 1", loadedTask1.getName(), "Название Task 1 должно совпадать.");
        assertEquals(Status.NEW, loadedTask1.getStatus(), "Статус Task 1 должен быть NEW.");
        assertEquals("Description of task 1", loadedTask1.getDescription(), "Описание Task 1 должно совпадать.");
        assertEquals("Task 2", loadedTask2.getName(), "Название Task 2 должно совпадать.");
        assertEquals(Status.IN_PROGRESS, loadedTask2.getStatus(), "Статус Task 2 должен быть IN_PROGRESS.");
        assertEquals("Description of task 2", loadedTask2.getDescription(), "Описание Task 2 должно совпадать.");
        assertEquals("Epic 1", loadedEpic.getName(), "Название Epic должно совпадать.");
        assertEquals(Status.IN_PROGRESS, loadedEpic.getStatus(), "Статус Epic должен быть IN_PROGRESS.");
        assertEquals("Description of epic 1", loadedEpic.getDescription(), "Описание Epic должно совпадать.");
        assertEquals("Subtask 1", loadedSubtask1.getName(), "Название Subtask 1 должно совпадать.");
        assertEquals(Status.NEW, loadedSubtask1.getStatus(), "Статус Subtask 1 должен быть NEW.");
        assertEquals("Description of subtask 1", loadedSubtask1.getDescription(), "Описание Subtask 1 должно совпадать.");
        assertEquals(3, loadedSubtask1.getEpicID(), "EpicId для Subtask 1 должен быть равен 3.");
        assertEquals("Subtask 2", loadedSubtask2.getName(), "Название Subtask 2 должно совпадать.");
        assertEquals(Status.DONE, loadedSubtask2.getStatus(), "Статус Subtask 2 должен быть DONE.");
        assertEquals("Description of subtask 2", loadedSubtask2.getDescription(), "Описание Subtask 2 должно совпадать.");
        assertEquals(3, loadedSubtask2.getEpicID(), "EpicId для Subtask 2 должен быть равен 3.");
    }
}
