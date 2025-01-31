package service;

import model.*;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    //создаем сущности
    void createEpic(Epic epic);

    void createSubtask(SubTask subtask, int epicId);

    void createTask(Task task);

    //удаление списка всех сущностей
    void clearEpicList();

    void clearSubtaskList();

    void clearTaskList();

    //получение списка всех сущностей
    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubtasks();

    ArrayList<Task> getTasks();

    //удаление сущностей по ID
    void deleteEpic(int id);

    void deleteSubtask(int id);

    void deleteTask(int id);

    //получение сущностей по ID
    Epic getEpicByID(int id);

    SubTask getSubtaskByID(int id);

    Task getTaskByID(int id);

    //обновление сущностей
    void updateEpic(Epic epic);

    void updateSubtask(SubTask subtask);

    void updateTask(Task task);

    List<Task> getHistory();

    // Получаем все подзадачи Эпика
    List<SubTask> getSubtasksOfEpic(int epicId);

    // Новый метод: задачи в порядке приоритета
    List<Task> getPrioritizedTasks();
}
