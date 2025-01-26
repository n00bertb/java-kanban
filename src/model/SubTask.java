package model;

import java.time.Duration;
import java.time.LocalDateTime;


public class SubTask extends Task {

    private final int epicID;

    public SubTask(String name, String description, Duration duration, LocalDateTime startTime, int epicID) {
        super(name, description, duration, startTime);
        this.epicID = epicID;
    }

    public SubTask(String name, String description, Status status, Duration duration, LocalDateTime startTime, int epicID) {
        super(name, description, status, duration, startTime);
        this.epicID = epicID;
    }

    public SubTask(String name, String description, int id, Status status, Duration duration, LocalDateTime startTime, int epicID) {
        super(name, description, id, status, duration, startTime);
        this.epicID = epicID;
    }

    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return "taskmanager.model.Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", epicID=" + epicID +
                ", status=" + getStatus() +
                ", duration=" + getDuration().toMinutes() +
                ", startTime=" + getStartTime() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        if (!super.equals(o)) return false;
        SubTask subtask = (SubTask) o;
        return epicID == subtask.epicID;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + epicID;
    }
}
