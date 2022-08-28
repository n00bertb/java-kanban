package manager;

import java.io.File;

public class Managers {

    private Managers () {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
        }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefaultBacked() {
        return new FileBackedTasksManager(new File("test.history.csv"));
    }

}