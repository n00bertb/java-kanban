package service;

import model.*;

import exceptions.ManagerSaveException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public static void main(String[] args) {
        File file = new File("src/resources/tasks.csv");

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs(); // Создает каталог resources, если он не существует
        }

        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Task 1", "Description of task 1");
        Task task2 = new Task("Task 2", "Description of task 2", Status.IN_PROGRESS);
        manager.createTask(task1);
        manager.createTask(task2);


        Epic epic1 = new Epic("Epic 1", "Description of epic 1");
        manager.createEpic(epic1);

        SubTask subtask1 = new SubTask("Subtask 1", "Description of subtask 1", epic1.getId());
        SubTask subtask2 = new SubTask("Subtask 2", "Description of subtask 2", Status.DONE, epic1.getId());

        manager.createSubtask(subtask1, epic1.getId());
        manager.createSubtask(subtask2, epic1.getId());

        System.out.println("Данные начального менеджера:");
        System.out.println("Задачи: " + manager.getTasks());
        System.out.println("Эпики: " + manager.getEpics());
        System.out.println("Подзадачи: " + manager.getSubtasks());

        // Создаем новый менеджер, загружая данные из того же файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        System.out.println("\nДанные загруженного менеджера:");
        System.out.println("Задачи: " + loadedManager.getTasks());
        System.out.println("Эпики: " + loadedManager.getEpics());
        System.out.println("Подзадачи: " + loadedManager.getSubtasks());

        // Сравниваем данные старого и нового менеджера
        if (manager.getTasks().equals(loadedManager.getTasks()) &&
                manager.getEpics().equals(loadedManager.getEpics()) &&
                manager.getSubtasks().equals(loadedManager.getSubtasks())) {
            System.out.println("\nВсе данные совпадают между исходным и загруженным менеджерами.");
        } else {
            System.out.println("\nДанные не совпадают между исходным и загруженным менеджерами.");
        }
    }

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (SubTask subtask : getSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    private String toString(Task task) {
        TaskType type = TaskType.TASK;
        if (task instanceof Epic) {
            type = TaskType.EPIC;
        } else if (task instanceof SubTask) {
            type = TaskType.SUBTASK;
        }
        String epicId = (task instanceof SubTask) ? String.valueOf(((SubTask) task).getEpicID()) : "";
        return String.join(",", String.valueOf(task.getId()), type.toString(), task.getName(), task.getStatus().toString(), task.getDescription(), epicId);
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                return new Task(name, description, id, status);
            case EPIC:
                return new Epic(name, description, id, status);
            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                return new SubTask(name, description, id, status, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines.subList(1, lines.size())) {
                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.createEpic((Epic) task);
                } else if (task instanceof SubTask) {
                    manager.createSubtask((SubTask) task, ((SubTask) task).getEpicID());
                } else {
                    manager.createTask(task);
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }
        return manager;
    }

    @Override
    public void createTask(Task task) {
        if (task.getId() == 0) {
            super.createTask(task);
            save();
        } else {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic.getId() == 0) {
            super.createEpic(epic);
            save();
        } else {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void createSubtask(SubTask subtask, int epicId) {
        if (subtask.getId() == 0) {
            super.createSubtask(subtask, epicId);
            save();
        } else {
            subtasks.put(subtask.getId(), subtask);
        }
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void clearTaskList() {
        super.clearTaskList();
        save();
    }

    @Override
    public void clearEpicList() {
        super.clearEpicList();
        save();
    }

    @Override
    public void clearSubtaskList() {
        super.clearSubtaskList();
        save();
    }
}
