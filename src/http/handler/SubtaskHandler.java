package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import menegers.InMemoryTaskManager;
import tasks.Subtask;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private final InMemoryTaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(InMemoryTaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (method) {
            case "GET":
                getSubtask(exchange, path); // Обработка GET запроса
                break;
            case "POST":
                createSubtask(exchange); // Обработка POST запроса
                break;
            case "PUT":
                updateSubtask(exchange); // Обработка PUT запроса
                break;
            case "DELETE":
                deleteSubtask(exchange, path); // Обработка DELETE запроса
                break;
            default:
                sendResponse(exchange, "Метод не поддерживается", 405); // Неподдерживаемый метод
        }
    }

    private void getSubtask(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/subtasks/\\d+")) {
            int id = Integer.parseInt(path.substring(10));
            Subtask subtask = taskManager.getSubtask(id);
            if (subtask != null) {
                sendResponse(exchange, gson.toJson(subtask), 200);
            } else {
                sendResponse(exchange, "Подзадача не найдена", 404);
            }
        } else if ("/subtasks".equals(path)) {
            sendResponse(exchange, gson.toJson(taskManager.getSubtasks()), 200);
        } else {
            sendResponse(exchange, "Некорректный запрос", 400);
        }
    }

    private void createSubtask(HttpExchange exchange) throws IOException {
        try (InputStreamReader reader =
                     new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Subtask subtask = gson.fromJson(reader, Subtask.class);
            if (subtask != null) {
                Subtask createdSubtask = taskManager.createSubtask(subtask);
                sendResponse(exchange, gson.toJson(createdSubtask), 201);
            } else {
                sendResponse(exchange, "Некорректный JSON", 400);
            }
        }
    }

    private void updateSubtask(HttpExchange exchange) throws IOException {
        try (InputStreamReader reader =
                     new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Subtask subtask = gson.fromJson(reader, Subtask.class);
            if (subtask != null && taskManager.getSubtask(subtask.getId()) != null) {
                taskManager.updateSubtask(subtask);
                sendResponse(exchange, "Подзадача обновлена", 200);
            } else {
                sendResponse(exchange, "Подзадача не найдена или некорректные данные", 400);
            }
        }
    }

    private void deleteSubtask(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/subtasks/\\d+")) {
            int id = Integer.parseInt(path.substring(10));
            taskManager.deleteSubtask(id);
            sendResponse(exchange, "Подзадача удалена", 200);
        } else if ("/subtasks".equals(path)) {
            taskManager.deleteSubtasks();
            sendResponse(exchange, "Все подзадачи удалены", 200);
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

