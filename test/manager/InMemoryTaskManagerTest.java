package manager;

import menegers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void createAndGetTask() {
        Task task = new Task("Задача1","Cделать тесты",TaskStatus.NEW);
        taskManager.createTask(task);

        Task savedTask = taskManager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают в списке.");
    }

    @Test
    void createAndGetEpic() {
        // Создаем Epic
        Epic epic = new Epic("Test Epic", "Test Epic Description");
        taskManager.createEpic(epic);

        // Получаем Epic по id
        Epic savedEpic = taskManager.getEpic(epic.getId());

        // Проверяем, что Epic существует и совпадает с созданным
        assertNotNull(savedEpic, "Epic не найден.");
        assertEquals(epic, savedEpic, "Epic не совпадают.");

        // Проверяем, что Epic добавился в список Epic
        List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Epics не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество Epic.");
        assertEquals(epic, epics.get(0), "Epic не совпадают в списке.");
    }

    @Test
    void createAndGetSubtask() {
        // Создаем Epic (Subtask не может существовать без Epic)
        Epic epic = new Epic("Test Epic", "Test Epic Description");
        taskManager.createEpic(epic);

        // Создаем Subtask
        Subtask subtask = new Subtask("Test Subtask", "Test Subtask Description", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        // Получаем Subtask по id
        Subtask savedSubtask = taskManager.getSubtask(subtask.getId());

        // Проверяем, что Subtask существует и совпадает с созданным
        assertNotNull(savedSubtask, "Subtask не найдена.");
        assertEquals(subtask, savedSubtask, "Subtask не совпадают.");

        // Проверяем, что Subtask добавился в список Subtask
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Subtasks не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество Subtask.");
        assertEquals(subtask, subtasks.get(0), "Subtask не совпадают в списке.");
    }
}