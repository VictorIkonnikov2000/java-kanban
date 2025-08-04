package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import menegers.InMemoryTaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.net.HttpURLConnection;

public class BaseHttpHandler {

    protected static final Gson gson = new Gson();
    protected final InMemoryTaskManager taskManager;


    public BaseHttpHandler(InMemoryTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void sendJson(HttpExchange exchange, int statusCode, String response) throws IOException {
        if (response == null) {
            response = "";
        }

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }


    protected void sendText(HttpExchange exchange, String response) throws IOException {
        sendJson(exchange, HttpURLConnection.HTTP_OK, response);
    }


    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        sendJson(exchange, HttpURLConnection.HTTP_NOT_FOUND, message);
    }


    protected void sendHasIntersections(HttpExchange exchange, String message) throws IOException {
        sendJson(exchange, HttpURLConnection.HTTP_NOT_ACCEPTABLE, message);
    }


    protected void sendServerError(HttpExchange exchange, String message) throws IOException {
        sendJson(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, message);
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
