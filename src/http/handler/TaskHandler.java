package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import menegers.InMemoryTaskManager;
import tasks.Task;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final InMemoryTaskManager taskManager;
    private final Gson gson;

    public TaskHandler(InMemoryTaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (method) {
            case "GET":
                getTask(exchange, path);
                break;
            case "POST":
                createTask(exchange);
                break;
            case "PUT":
                updateTask(exchange);
                break;
            case "DELETE":
                deleteTask(exchange, path);
                break;
            default:
                sendResponse(exchange, "Метод не поддерживается", 405);
        }
    }

    private void getTask(HttpExchange exchange, String path) throws IOException {
        if ("/tasks".equals(path)) { // Получение всех задач
            List<Task> tasks = taskManager.getTasks();
            sendResponse(exchange, gson.toJson(tasks), 200);
        } else if (path.matches("/tasks/\\d+")) { // Получение задачи по id
            int id = Integer.parseInt(path.substring(7));
            Task task = taskManager.getTask(id);
            if (task != null) {
                sendResponse(exchange, gson.toJson(task), 200);
            } else {
                sendResponse(exchange, "Задача не найдена", 404);
            }
        } else {
            sendResponse(exchange, "Некорректный запрос", 400);
        }
    }

    private void createTask(HttpExchange exchange) throws IOException {
        try (InputStreamReader reader =
                     new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Task task = gson.fromJson(reader, Task.class);
            if (task != null) {
                Task createdTask = taskManager.createTask(task);
                sendResponse(exchange, gson.toJson(createdTask), 201);
            } else {
                sendResponse(exchange, "Некорректный JSON", 400);
            }
        }
    }

    private void updateTask(HttpExchange exchange) throws IOException {
        try (InputStreamReader reader =
                     new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Task task = gson.fromJson(reader, Task.class);
            if (task != null && taskManager.getTask(task.getId()) != null) {
                taskManager.updateTask(task);
                sendResponse(exchange, "Задача обновлена", 200);
            } else {
                sendResponse(exchange, "Задача не найдена или некорректные данные", 400);
            }
        }
    }

    private void deleteTask(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/tasks/\\d+")) { // Удаление задачи по id
            int id = Integer.parseInt(path.substring(7));
            taskManager.deleteTask(id);
            sendResponse(exchange, "Задача удалена", 200);
        } else if ("/tasks".equals(path)) { // Удаление всех задач
            taskManager.deleteTasks();
            sendResponse(exchange, "Все задачи удалены", 200);
        } else {
            sendResponse(exchange, "Некорректный запрос", 400);
        }
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode)
            throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
        exchange.close();
    }
}

