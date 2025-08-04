package http.handler;



import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import menegers.InMemoryTaskManager;
import tasks.Epic;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {


    public EpicHandler(InMemoryTaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (method) {
            case "GET":
                getEpic(exchange, path);
                break;
            case "POST":
                createEpic(exchange);
                break;
            case "PUT":
                updateEpic(exchange);
                break;
            case "DELETE":
                deleteEpic(exchange, path);
                break;
            default:
                sendResponse(exchange, "Method Not Allowed", 405);
        }
    }

    private void getEpic(HttpExchange exchange, String path) throws IOException {
        if ("/epics".equals(path)) {
            List<Epic> epics = taskManager.getEpics();
            sendResponse(exchange, gson.toJson(epics), 200);
        } else if (path.matches("/epics/\\d+")) {
            int id = Integer.parseInt(path.substring(7));
            Epic epic = taskManager.getEpic(id);
            if (epic != null) {
                sendResponse(exchange, gson.toJson(epic), 200);
            } else {
                sendResponse(exchange, "Epic Not Found", 404);
            }
        } else {
            sendResponse(exchange, "Bad Request", 400);
        }
    }

    private void createEpic(HttpExchange exchange) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Epic epic = gson.fromJson(reader, Epic.class);
            if (epic != null) {
                Epic createdEpic = taskManager.createEpic(epic);
                sendResponse(exchange, gson.toJson(createdEpic), 201);
            } else {
                sendResponse(exchange, "Bad JSON", 400);
            }
        }
    }

    private void updateEpic(HttpExchange exchange) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Epic epic = gson.fromJson(reader, Epic.class);
            if (epic != null && taskManager.getEpic(epic.getId()) != null) {
                taskManager.updateEpic(epic);
                sendResponse(exchange, "Epic Updated", 200);
            } else {
                sendResponse(exchange, "Epic Not Found or Bad Data", 400);
            }
        }
    }

    private void deleteEpic(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/epics/\\d+")) {
            int id = Integer.parseInt(path.substring(7));
            taskManager.deleteEpic(id);
            sendResponse(exchange, "Epic Deleted", 200);
        } else if ("/epics".equals(path)) {
            taskManager.deleteEpics();
            sendResponse(exchange, "All Epics Deleted", 200);
        } else {
            sendResponse(exchange, "Bad Request", 400);
        }
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
        exchange.close();
    }
}
