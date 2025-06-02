package menegers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final List<Task> history = new ArrayList<>();
    private final int MAX_LIMIT = 10;

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history); // альтернативный способ:return List.copyOf(history);
    }

    @Override
    public void addHistory(Task task) {
        if (history.size() == MAX_LIMIT) history.remove(0);
        history.add(task);
    }
}
