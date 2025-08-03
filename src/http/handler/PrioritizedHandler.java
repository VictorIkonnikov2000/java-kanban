package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import menegers.InMemoryTaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private final InMemoryTaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(InMemoryTaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && "/prioritized".equals(path)) {
            getPrioritizedTasks(exchange); // Вызов метода для обработки GET-запроса.
        } else {
            sendResponse(exchange, "Метод не поддерживается или некорректный путь", 405);
        }
    }


    private void getPrioritizedTasks(HttpExchange exchange) throws IOException {
        List prioritizedTasks = taskManager.getPrioritizedTasks(); // Получаем список.
        String response = gson.toJson(prioritizedTasks); // Преобразуем в JSON.
        sendResponse(exchange, response, 200); // Отправляем ответ.
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

