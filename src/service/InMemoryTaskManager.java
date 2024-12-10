package service;

import model.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager;
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private int taskId = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public int getNewTaskId() {
        taskId++;
        return taskId;
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
        if (epics.containsKey(epicId)) {
            int id = getNewTaskId();
            subtask.setId(id);
            subtasks.put(id, subtask);
            epics.get(epicId).addSubtask(id); // Добавляем id подзадачи в эпик
            updateEpicStatus(epics.get(epicId)); // И обновляем статус
        } else {
            System.out.println("Эпик с таким ID не существует");
        }
    }

    @Override
    public void createTask(Task task) {
        int id = getNewTaskId();
        task.setId(id);
        tasks.put(id, task);
    }

    //удаление списка всех сущностей
    @Override
    public void clearEpicList() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtaskList() {
        subtasks.clear();
        // Обновление статуса всех эпиков
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epics.get(epic.getId()));
        }
    }

    @Override
    public void clearTaskList() {
        Set<Integer> setKeys = tasks.keySet();
        for (Integer key : setKeys) {
            historyManager.remove(key);
        }
        tasks.clear();
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
                subtasks.remove(subTaskIds);
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
            updateEpicStatus(epic);
            subtasks.remove(id);
        } else {
            System.out.println("Подзадача с таким ID не существует");
        }
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
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
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicID()));
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    //обновление статуса эпика
    private void updateEpicStatus(Epic epic) {
        boolean hasInProgress = false;
        boolean allDone = true;

        if (epics.get(epic.getId()).getSubtaskList().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        for (int subtaskID : epic.getSubtaskList()) {
            if (subtasks.get(subtaskID).getStatus() == Status.IN_PROGRESS) {
                hasInProgress = true;
            }
            if (subtasks.get(subtaskID).getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (hasInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}