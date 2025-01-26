package model;

import java.time.Duration;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIds = new ArrayList<>();


    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(String name, String description, int id, Status status) {
        super(name, description, id, status, Duration.ZERO, null);
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }


    public void addSubtask(int idSubtask) {
        subTaskIds.add(idSubtask);
    }

    public void clearSubtasks() {
        subTaskIds.clear();
    }

    public void deleteSubtask(int idSubtask) {
        subTaskIds.remove(subTaskIds.indexOf(idSubtask));
    }

    public ArrayList<Integer> getSubtaskList() {
        return subTaskIds;
    }

    @Override
    public String toString() {
        return "taskmanager.model.Epic{" +
                "name='" + getName() + '\'' +
                ", description = " + getDescription() + '\'' +
                ", id=" + getId() +
                ", subtaskList.size = " + subTaskIds.size() +
                ", status = " + getStatus() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        Epic epic = (Epic) o;
        return getId() == epic.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }
}