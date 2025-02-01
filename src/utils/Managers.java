package utils;

import service.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}