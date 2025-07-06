package menegers;

import java.io.File;

public class Managers {
    public static TaskManager getDefoult() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefoultHistory() {
        return new InMemoryHistoryManager();
    }
}
