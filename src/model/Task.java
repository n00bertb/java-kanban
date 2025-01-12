package model;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(String title, String text, Status status) {
        this.name = title;
        this.description = text;
        this.status = status;
    }

    public Task(String title, String text, int id, Status status) {
        this.name = title;
        this.description = text;
        this.status = status;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = hash * 31 + id;
        if (name != null) {
            hash = hash + name.hashCode();
        }
        if (description != null) {
            hash = hash + description.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        return "taskmanager.model.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
