package manager;

import task.Epic;
import task.SubTask;
import task.Task;
import task.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    protected int taskId = 10;
    protected int epicId = 20;
    protected int subTaskId = 30;
    protected final Map<Integer, Task> taskData = new HashMap<>();
    protected final Map<Integer, Epic> epicData = new HashMap<>();
    protected final Map<Integer, SubTask> subTaskData = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    protected final List<LocalDateTime> allDates = new ArrayList<>();

    @Override
    public int addNewTask(Task task) { // добавляет задачу в мапу
        task.setId(taskId++);
        taskData.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic) { // добавляет задачу в мапу
        epic.setId(epicId++);
        epicData.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int addNewSubTask(SubTask subTask) { // добавляет задачу в мапу и её айди в лист эпика
        subTask.setId(subTaskId++);
        subTaskData.put(subTask.getId(), subTask);
        epicData.get(subTask.getEpicId()).addSubTaskIds(subTask.getId());
        findEpicStatus(subTask.getEpicId()); // пересчитал статус эпика
        return subTask.getId();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskData.get(id);// получает и возвращает задачу по id
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) { // получает и возвращает задачу по id
        SubTask subTask = subTaskData.get(id);
        if (subTask!= null) {
            historyManager.add(subTask);
        }
        return subTask;
    }

    @Override
    public Epic getEpicById(int id) { // получает и возвращает задачу по id
        Epic epic = epicData.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public void deleteAllTasks() { // удаляет все задачи из мапы
        for (int ids : taskData.keySet()) {
            historyManager.remove(ids);
        }
        taskData.clear();
    }

    @Override
    public void deleteAllSubTasks() { // удаляет все задачи из мапы
        for (int ids : subTaskData.keySet()) {
            historyManager.remove(ids);
        }
        subTaskData.clear();
        for (int id : epicData.keySet()) {
            epicData.get(id).deleteSubTaskIds();// чистит списки айди в эпиках
            epicData.get(id).setStatus(Status.NEW);// обновляет статус эпика
            findEpicTime(id);
        }

    }

    @Override
    public void deleteAllEpics() { // удаляет все эпики и сабтаски из мап
        for (int ids : epicData.keySet()) {
            historyManager.remove(ids);
        }
        epicData.clear();
        deleteAllSubTasks();
    }

    @Override
    public void deleteTaskById(int id) { // удаляет задачу из мапы по id
        historyManager.remove(id);
        taskData.remove(id);
    }

    @Override
    public void deleteSubTaskById(Integer id) { // удаляет сабтаск из мапы и из листа в эпике и пересчитывает статус
        Integer epicId = subTaskData.get(id).getEpicId(); // сохранил айди чтобы не было NullPointerException
        historyManager.remove(id);
        subTaskData.remove(id);
        epicData.get(epicId).deleteSubTaskFromList(id);
        findEpicTime(epicId);
        findEpicStatus(epicId);
    }

    @Override
    public void deleteEpicById(Integer id) { // удаляет задачу из мапы по id
        for (Integer subId : getEpicById(id).getSubTaskIds()) { // и чистит мапу сабтасков по эпикАйди
            subTaskData.remove(subId);
            historyManager.remove(subId);
        }
        historyManager.remove(id);
        epicData.remove(id);
    }


    @Override
    public void updateTask(Task task) { // перезаписывает task под тем же id
        checkTaskDate(task.getStartTime());
        if (taskData.get(task.getId()) != null)
            taskData.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(SubTask subTask) { // перезаписывает subTask под тем же id
        checkTaskDate(subTask.getStartTime());
        if (subTaskData.get(subTask.getId()) != null && !subTaskData.get(subTask.getId())
                .getStatus().equals(subTask.getStatus())) { // если не null и статус изменился
            subTaskData.put(subTask.getId(), subTask); // заменили сабтаск
            findEpicTime(subTask.getEpicId()); // пересчитали время эпика
            findEpicStatus(subTaskData.get(subTask.getId()).getEpicId()); // и пересчитали статус эпика
            return;
        }
            subTaskData.put(subTask.getId(), subTask); // если статус не изменился, только заменили сабтаск
    }

    @Override
    public void updateEpic(Epic epic) { // перезаписывает epic под тем же id
        if (epicData.get(epic.getId()) != null)
            epicData.put(epic.getId(), epic);
    }

    @Override
    public List<Task> getTask() { // возвращает лист тасков
        return  List.copyOf(taskData.values());
    }

    @Override
    public List<Epic> getEpic() { // возвращает лист эпиков
        return List.copyOf(epicData.values());
    }

    @Override
    public List<SubTask> getSubTask() { // возвращает лист сабтасков
        return List.copyOf(subTaskData.values());
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) {
        List<SubTask> epicSubTasks = new ArrayList<>();
        for (Integer id : subTaskData.keySet()) {
            if (subTaskData.get(id).getEpicId().equals(epicId)) {
                epicSubTasks.add(subTaskData.get(id));
            }
        }
        return epicSubTasks;
    }
    @Override
    public List<Task> getPrioritizedTasks() { // возвращает лист отсортированных тасок
        List<Task> unsortedTasks = new ArrayList<>();
        unsortedTasks.addAll(subTaskData.values());
        unsortedTasks.addAll(taskData.values());
        unsortedTasks.addAll(epicData.values());
        unsortedTasks.sort(Comparator.comparing(Task::getStartTime));
        return unsortedTasks;
    }
    @Override
    public void checkTaskDate(LocalDateTime dateTime) { // проверка на совпадение времени тасок
            if (allDates.contains(LocalDateTime.of(dateTime.getYear(), dateTime.getMonthValue(),
                    dateTime.getDayOfMonth(), dateTime.getHour(), 0, 0)))
                throw new IllegalArgumentException("Задача с таким временем уже существует." + dateTime);

            allDates.add(LocalDateTime.of(dateTime.getYear(), dateTime.getMonthValue(),
                    dateTime.getDayOfMonth(), dateTime.getHour(), 0, 0));
    }

    @Override
    public void findEpicTime(int epicId) { // ищет время начала, продолжительности и конца эпика
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");
        if(getEpicSubTasks(epicId).isEmpty()) {
            getEpicById(epicId).setDuration(Duration.ofNanos(0));
            return;
        }

        List<LocalDateTime> subStartTimes = getEpicSubTasks(epicId).stream()
                .map(Task::getStartTime).sorted(LocalDateTime::compareTo)
                .collect(Collectors.toCollection(ArrayList::new));
        getEpicById(epicId).setStartTime(subStartTimes.get(0));

        List<LocalDateTime> subEndTimes = getEpicSubTasks(epicId).stream()
                .map(Task::getEndTime)
                .map(sd -> LocalDateTime.parse(sd, formatter)).sorted(LocalDateTime::compareTo)
                .collect(Collectors.toCollection(ArrayList::new));
        getEpicById(epicId).setEndTime(subEndTimes.get(subEndTimes.size() - 1));

        Duration epicDuration = Duration.ofNanos(0);
        for (int id : epicData.get(epicId).getSubTaskIds()){
            epicDuration = epicDuration.plus(subTaskData.get(id).getDuration());
        }
        getEpicById(epicId).setDuration(epicDuration);
    }

    @Override
    public void findEpicStatus(int epicId) { // вычисляет статус эпика
        List<Status> statusList = new ArrayList<>();
        for (Integer id : epicData.get(epicId).getSubTaskIds()) {
            statusList.add(subTaskData.get(id).getStatus());
        }
        int statusIndex = 0;
        for (int i = 0; i < statusList.size(); i++){
            if (statusList.get(i).equals(Status.DONE)) {
                statusIndex += 2;
            } else if (statusList.get(i).equals(Status.IN_PROGRESS)) {
                statusIndex++;
            }
        }
        if (statusIndex == 0) {
            epicData.get(epicId).setStatus(Status.NEW);
        } else if (statusIndex == (statusList.size() * 2)) {
            epicData.get(epicId).setStatus(Status.DONE);
        } else {
            epicData.get(epicId).setStatus(Status.IN_PROGRESS);
        }
    }
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}