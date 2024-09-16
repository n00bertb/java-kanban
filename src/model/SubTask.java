package model;

public class SubTask extends Task {

    private final int epicID;
    public SubTask(String name, String description, int epicID) {
        super(name, description);
        this.epicID = epicID;
    }
    public SubTask(int id, String name, String description, Status status, int epicID) {
        super(id, name, description, status);
        this.epicID = epicID;
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
                '}';
    }
}
