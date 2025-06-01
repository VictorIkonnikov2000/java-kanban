package manager;

import menegers.HistoryManager;
import menegers.InMemoryHistoryManager;
import menegers.Managers;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {



    @Test
    void addHistory_shouldAddTasksToHistory() {
        HistoryManager historyManager = Managers.getDefoultHistory();
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW);

        historyManager.addHistory(task1);
        historyManager.addHistory(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "History should contain two tasks.");
        assertEquals(task1, history.get(0), "First task in history should be task1.");
        assertEquals(task2, history.get(1), "Second task in history should be task2.");
    }

    @Test
    void addHistory_shouldRespectMaxLimit() {
        HistoryManager historyManager = Managers.getDefoultHistory();
        int maxLimit = 10;
        for (int i = 0; i < maxLimit + 5; i++) {
            historyManager.addHistory(new Task("Task " + i, "Description " + i, TaskStatus.NEW));
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(maxLimit, history.size(), "History should respect the max limit.");
    }
}