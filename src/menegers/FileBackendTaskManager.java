package menegers;

import exceptions.SaveFailedException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackendTaskManager extends  InMemoryTaskManager {
    private final File file;
    private Integer counter;

    public FileBackendTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public Task deleteTask(int id) {
        Task deletedTask = super.deleteTask(id);
        save();
        return deletedTask;
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }


    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Task task : getTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }

            for (Subtask subtask : getSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }

            for (Epic epic : getEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }
            writer.newLine();
        } catch (IOException e) {
            //Напишите своё исключение
            throw new SaveFailedException("Ошибка при сохранении файла" + e.getMessage());
        }
    }

    public static String toString(Task task) {
        return task.getId() + "," +
                "TASK," +
                task.getName() + "," +
                task.getTaskStatus() + "," +
                task.getDescription();
    }

    public static String toString(Epic epic) {
        return epic.getId() + "," +
                "EPIC," +
                epic.getName() + "," +
                epic.getTaskStatus() + "," +
                epic.getDescription();
    }

    public static String toString(Subtask subtask) {
        return subtask.getId() + "," +
                "SUBTASK," +
                subtask.getName() + "," +
                subtask.getTaskStatus() + "," +
                subtask.getDescription() + "," +
                subtask.getEpicId();
    }

    public static FileBackendTaskManager loadFromFile(File file) {
        FileBackendTaskManager taskManager = new FileBackendTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() > 1) {
                for (int i = 1; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (!line.isEmpty()) {
                        Task task = fromString(line);
                        if (task != null) {
                            switch (task.getClass().getSimpleName()) {
                                case "Task":
                                    taskManager.createTask((Task) task);
                                    break;
                                case "Epic":
                                    taskManager.createEpic((Epic) task);
                                    break;
                                case "Subtask":
                                    taskManager.createSubtask((Subtask) task);
                                    break;
                            }
                            if (task.getId() > taskManager.counter) {
                                taskManager.counter = task.getId();
                            }
                        }
                    }
                }
                taskManager.counter++;
            }

        } catch (IOException e) {
            throw new SaveFailedException("Ошибка при чтении файла: " + e.getMessage());
        }
        return taskManager;
    }

    public static Task fromString(String line) {
        String[] data = line.split(","); // Разделяем строку на элементы
        if (data.length < 5) return null;

        int id = Integer.parseInt(data[0]); // ID задачи
        String type = data[1]; // Тип задачи
        String name = data[2]; // Имя задачи
        TaskStatus status = TaskStatus.valueOf(data[3]); // Статус задачи
        String description = data[4]; // Описание задачи

        switch (type) {
            case "TASK":
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case "EPIC":
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setTaskStatus(status);
                return epic;
            case "SUBTASK":
                if (data.length < 6) return null;
                int epicId = Integer.parseInt(data[5]);
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                return subtask;
            default:
                return null;
        }
    }


}
