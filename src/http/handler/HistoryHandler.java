package http.handler;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import menegers.InMemoryTaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class HistoryHandler extends BaseHttpHandler implements HttpHandler {


    public HistoryHandler(InMemoryTaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && "/history".equals(path)) {
            getHistory(exchange); // Вызов метода для обработки GET-запроса.
        } else {
            sendResponse(exchange, "Метод не поддерживается или некорректный путь", 405);
        }
    }


    private void getHistory(HttpExchange exchange) throws IOException {
        List history = taskManager.getHistory(); // Получаем историю задач из taskManager.
        String response = gson.toJson(history); // Преобразуем историю задач в JSON-строку.
        sendResponse(exchange, response, 200); // Отправляем JSON-строку как ответ.
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
