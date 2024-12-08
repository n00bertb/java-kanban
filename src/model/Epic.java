package model;

import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subTaskIds = new ArrayList<>();
    public Epic(String name, String description) {
        super(name, description,Status.NEW);
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
}