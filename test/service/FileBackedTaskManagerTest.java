package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

        Task task1 = new Task("Task 1", "Description 1");
        Epic epic1 = new Epic("Epic 1", "Description 2");
        SubTask subtask1 = new SubTask("Subtask 1", "Description 3", 2);

        manager.createTask(task1);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1,epic1.getId());

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
                "id,type,name,status,description,epic",
                "1,TASK,Task1,NEW,Description task1,",
                "2,EPIC,Epic2,NEW,Description epic2,",
                "3,SUBTASK,Sub Task2,DONE,Description sub task3,2"
        );

        Files.writeString(tempFile.toPath(), csvContent);

        // Загружаем менеджер из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем загруженные данные
        Task loadedTask = loadedManager.getTaskByID(1);
        Epic loadedEpic = loadedManager.getEpicByID(2);
        SubTask loadedSubtask = loadedManager.getSubtaskByID(3);

        assertNotNull(loadedTask, "Задача должна быть загружена.");
        assertNotNull(loadedEpic, "Эпик должен быть загружен.");
        assertNotNull(loadedSubtask, "Подзадача должна быть загружена.");

        assertEquals("Task1", loadedTask.getName());
        assertEquals(Status.NEW, loadedTask.getStatus());

        assertEquals("Epic2", loadedEpic.getName());
        assertEquals(Status.NEW, loadedEpic.getStatus());

        assertEquals("Sub Task2", loadedSubtask.getName());
        assertEquals(Status.DONE, loadedSubtask.getStatus());
        assertEquals(2, loadedSubtask.getEpicID(), "ID эпика у подзадачи должен совпадать.");
    }
}
