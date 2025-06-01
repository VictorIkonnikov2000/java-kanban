package menegers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int generatorId = 1;
    private HistoryManager historyManager = Managers.getDefoultHistory();

    private int getNextId() {
        return generatorId++;
    }



    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        addHistory(task);
        return task;
    }


    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        addHistory(epic);
        return epic;
    }


    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        addHistory(subtask);
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
        return tasks.remove(id);
    }



    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
        }
    }


    @Override
    public void deleteSubtask(int id) {
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
        tasks.clear();
    }


    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }


    @Override
    public void deleteSubtasks() {
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

        ArrayList<Subtask> epicSubtasks = epic.getSubtasks();
        int newCount = 0;
        int completedCount = 0;
        for (Subtask subtask : epicSubtasks) {
            if (subtask.getTaskStatus().equals(TaskStatus.NEW))
                newCount++;
            if (subtask.getTaskStatus().equals(TaskStatus.DONE)) {
                completedCount++;
            }
        }
        if (epicSubtasks.isEmpty() || newCount == epicSubtasks.size()) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else if (completedCount == epicSubtasks.size()) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
    }



    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return new ArrayList<>(epic.getSubtasks());
        }
        return new ArrayList<>();
    }


    public List<Task> getHistory(){
        return historyManager.getHistory();
    }

    public void addHistory(Task task){
        historyManager.addHistory(task);
    }


}




