package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{
    private List<SubTask> subtaskList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }
    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }
    public void addSubtask(SubTask subtask) {
        subtaskList.add(subtask);
    }
    public void clearSubtasks() {
        subtaskList.clear();
    }

    public List<SubTask> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(List<SubTask> subtaskList) {
        this.subtaskList = subtaskList;
    }

    @Override
    public String toString() {
        return "taskmanager.model.Epic{" +
                "name= " + getName() + '\'' +
                ", description = " + getDescription() + '\'' +
                ", id=" + getId() +
                ", subtaskList.size = " + subtaskList.size() +
                ", status = " + getStatus() +
                '}';
    }
}