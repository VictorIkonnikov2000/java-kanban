package menegers;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
     List<Task> getHistory();
     void addHistory(Task task);
}
