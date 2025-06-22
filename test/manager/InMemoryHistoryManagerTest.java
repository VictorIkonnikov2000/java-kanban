package manager;

import menegers.HistoryManager;
import menegers.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task1.setId(1);
        task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS);
        task2.setId(2);
        task3 = new Task("Task 3", "Description 3", TaskStatus.DONE);
        task3.setId(3);
    }

    @Test
    void addHistory_shouldAddTaskToHistory() {
        historyManager.addHistory(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        assertEquals(task1, history.get(0), "Задача в истории должна быть task1.");
    }

    @Test
    void addHistory_shouldNotAddNullTask() {
        historyManager.addHistory(null);
        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не должна содержать задач.");
    }

    @Test
    void addHistory_shouldNotAddDuplicateTasks() {
        historyManager.addHistory(task1);
        historyManager.addHistory(task1); // Добавляем задачу с тем же ID повторно
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать только одну задачу (обновленную).");
        assertEquals(task1, history.get(0), "Задача в истории должна быть task1.");
    }

    @Test
    void remove_shouldRemoveTaskFromHistory() {
        historyManager.addHistory(task1);
        historyManager.addHistory(task2);
        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу после удаления.");
        assertEquals(task2, history.get(0), "В истории должна остаться task2.");
    }

    @Test
    void remove_shouldHandleRemovingNonExistentTask() {
        historyManager.addHistory(task1);
        historyManager.remove(999); // Пытаемся удалить задачу, которой нет в истории
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История не должна измениться.");
        assertEquals(task1, history.get(0), "В истории должна остаться task1.");
    }

    @Test
    void getHistory_shouldReturnEmptyListWhenHistoryIsEmpty() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой.");
    }

    @Test
    void getHistory_shouldReturnTasksInCorrectOrder() {
        historyManager.addHistory(task1);
        historyManager.addHistory(task2);
        historyManager.addHistory(task3);
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "История должна содержать три задачи.");
        assertEquals(task1, history.get(0), "Первая задача должна быть task1.");
        assertEquals(task2, history.get(1), "Вторая задача должна быть task2.");
        assertEquals(task3, history.get(2), "Третья задача должна быть task3.");
    }
}