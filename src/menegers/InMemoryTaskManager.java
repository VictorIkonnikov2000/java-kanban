package menegers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int generatorId = 1;
    private HistoryManager historyManager = Managers.getDefoultHistory();

    private TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    private int getNextId() {
        return generatorId++;
    }


    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            addHistory(task);
        }
        return task;
    }


    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            addHistory(epic);
        }
        return epic;
    }


    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            addHistory(subtask);
        }
        return subtask;
    }


    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }


    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }


    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }


    @Override
    public Task createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task;
    }


    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        return epic;
    }


    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null || !epics.containsKey(subtask.getEpicId())) {
            return null;
        }
        subtask.setId(getNextId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtasks().add(subtask);
            updateEpicStatus(epic.getId());
        }
        return subtask;
    }


    @Override
    public Task updateTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }


    @Override
    public Epic updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        return epic;
    }


    @Override
    public Subtask updateSubtask(Subtask subtask) {
        updateEpicStatus(subtask.getEpicId());
        subtasks.put(subtask.getId(), subtask);
        return subtask;
    }


    @Override
    public Task deleteTask(int id) {
        historyManager.remove(id);
        return tasks.remove(id);
    }


    @Override
    public void deleteEpic(int id) {
        historyManager.remove(id);
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                historyManager.remove(subtask.getId());
                subtasks.remove(subtask.getId());
            }
        }
    }


    @Override
    public void deleteSubtask(int id) {
        historyManager.remove(id);
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtasks().remove(subtask);
                updateEpicStatus(epic.getId());
            }
        }
    }


    @Override
    public void deleteTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }


    @Override
    public void deleteEpics() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
            Epic epic = epics.get(id);
            if (epic != null) {
                for (Subtask subtask : epic.getSubtasks()) {
                    historyManager.remove(subtask.getId());
                    subtasks.remove(subtask.getId());
                }
            }
        }
        epics.clear();
        subtasks.clear();
    }


    @Override
    public void deleteSubtasks() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }

        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }


    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> epicSubtasks = epic.getSubtasks();

        if (epicSubtasks.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }

        // Считаем статусы подзадач с использованием Stream API
        Map<TaskStatus, Long> statusCounts = epicSubtasks.stream()
                .map(Subtask::getTaskStatus)
                .collect(Collectors.groupingBy(status -> status, Collectors.counting()));

        long newCount = statusCounts.getOrDefault(TaskStatus.NEW, 0L);
        long doneCount = statusCounts.getOrDefault(TaskStatus.DONE, 0L);

        if (newCount == epicSubtasks.size()) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else if (doneCount == epicSubtasks.size()) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }


    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId); // Получаем Epic по его id.
        if (epic == null) {
            return new ArrayList<>(); // Если Epic не найден, возвращаем пустой список.
        }
        return epic.getSubtasks().stream()
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    public void addHistory(Task task) {
        historyManager.addHistory(task);
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    // Проверяет, пересекаются ли две задачи по времени
    private boolean isIntersect(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) return false;

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    // Проверяет, пересекается ли задача с другими задачами в списке
    private boolean hasIntersection(Task task) {
        return prioritizedTasks.stream().anyMatch(otherTask -> isIntersect(task, otherTask) && !task.equals(otherTask));
    }

}





