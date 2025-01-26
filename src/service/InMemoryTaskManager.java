package service;

import model.*;
import utils.Managers;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Collection;


public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    private int taskId = 0;

    public int getNewTaskId() {
        taskId++;
        return taskId;
    }

    private final Set<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
        if (task1.getStartTime() == null && task2.getStartTime() == null) return 0;
        if (task1.getStartTime() == null) return 1;
        if (task2.getStartTime() == null) return -1;
        return task1.getStartTime().compareTo(task2.getStartTime());
    });

    // Проверка пересечения задач
    private boolean isOverlapping(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private boolean isValidTask(Task newTask) {
        return prioritizedTasks.stream()
                .filter(existingTask -> !existingTask.equals(newTask)) // Исключаем текущую задачу
                .noneMatch(existingTask -> isOverlapping(newTask, existingTask)); // Проверяем пересечения
    }

    //создаем сущности
    @Override
    public void createEpic(Epic epic) {
        int id = getNewTaskId();
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void createSubtask(SubTask subtask, int epicId) {
        if (epics.containsKey(epicId) && isValidTask(subtask)) {
            int id = getNewTaskId();
            subtask.setId(id);
            subtasks.put(id, subtask);
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
            epics.get(epicId).addSubtask(id);
            updateEpicStatus(epics.get(epicId));
        } else {
            throw new IllegalArgumentException("Подзадача пересекается с другой задачей по времени выполнения.");
        }
    }

    @Override
    public void createTask(Task task) {
        if (isValidTask(task)) {
            int id = getNewTaskId();
            task.setId(id);
            tasks.put(id, task);
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        } else {
            throw new IllegalArgumentException("Задача пересекается с другой задачей по времени выполнения.");
        }
    }

    //удаление списка всех сущностей
    @Override
    public void clearEpicList() {
        List<SubTask> allSubtasks = new ArrayList<>(subtasks.values());
        subtasks.clear();
        allSubtasks.forEach(prioritizedTasks::remove);
        epics.clear();
    }

    @Override
    public void clearSubtaskList() {
        List<SubTask> allSubtasks = new ArrayList<>(subtasks.values());
        subtasks.clear();
        allSubtasks.forEach(prioritizedTasks::remove);
        // Обновление статуса всех эпиков
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epics.get(epic.getId()));
        }
    }

    @Override
    public void clearTaskList() {
        List<Task> allTasks = new ArrayList<>(tasks.values());//нахуя не понял
        Set<Integer> setKeys = tasks.keySet();
        for (Integer key : setKeys) {
            historyManager.remove(key);
        }
        tasks.clear();
        allTasks.forEach(prioritizedTasks::remove);//нахуя не понял
    }

    //получение списка всех сущностей
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    //удаление сущностей по ID
    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            for (int subTaskIds : epics.get(id).getSubtaskList()) {
                SubTask subtask = subtasks.remove(subTaskIds);
                prioritizedTasks.remove(subtask);
            }
            epics.get(id).clearSubtasks();
            epics.remove(id);
        } else {
            System.out.println("Эпик с таким ID не существует");
        }
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            int parentEpicId = subtasks.get(id).getEpicID();
            Epic epic = epics.get(parentEpicId);
            epic.deleteSubtask(id);
            SubTask subtask = subtasks.remove(id);
            prioritizedTasks.remove(subtask);
            updateEpicStatus(epic);
        } else {
            System.out.println("Подзадача с таким ID не существует");
        }
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.remove(id);
            prioritizedTasks.remove(task);
            historyManager.remove(id);  // Удаляем задачу из истории просмотров
        } else {
            System.out.println("Задача с таким ID не существует");
        }
    }

    //получение сущностей по ID
    @Override
    public Epic getEpicByID(int id) {
        if (epics.get(id) != null) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        } else {
            System.out.println("Эпик с таким ID не существует");
        }
        return null;
    }

    @Override
    public SubTask getSubtaskByID(int id) {
        if (subtasks.get(id) != null) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            System.out.println("Подзадача с таким ID не существует");
        }
        return null;
    }

    @Override
    public Task getTaskByID(int id) {
        if (tasks.get(id) != null) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else {
            System.out.println("Задача с таким ID не существует");
        }
        return null;
    }

    //обновление сущностей
    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epics.get(epic.getId()));
        } else {
            System.out.println("Эпик с таким ID не существует");
        }
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        if (subtasks.containsKey(subtask.getId()) && isValidTask(subtask)) {
            prioritizedTasks.remove(subtask);
            subtasks.put(subtask.getId(), subtask);
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
            updateEpicStatus(epics.get(subtask.getEpicID()));
        } else {
            throw new IllegalArgumentException("Подзадача пересекается с другой задачей по времени выполнения.");
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId()) && isValidTask(task)) {
            prioritizedTasks.remove(task);
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        } else {
            throw new IllegalArgumentException("Задача пересекается с другой задачей по времени выполнения.");
        }
    }

    //обновление статуса эпика
    private void updateEpicStatus(Epic epic) {
        Collection<Integer> tasksId = epic.getSubtaskList();
        if (tasksId.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else if (tasksId.stream()
                .map(subtasks::get)
                .anyMatch(
                        task -> task.getStatus() == Status.IN_PROGRESS || task.getStatus() == Status.NEW)) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.DONE);
        }
    }


    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}