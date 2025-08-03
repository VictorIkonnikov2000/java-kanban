package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import http.handler.*;
import menegers.InMemoryTaskManager;
import menegers.Managers;
import menegers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {

    private static final int PORT = 8080;
    private HttpServer httpServer;
    private TaskManager taskManager;
    private static Gson gson;


    public HttpTaskServer(TaskManager manager) throws IOException {
        taskManager = Managers.getDefoult(); // Получаем экземпляр TaskManager
        gson = new Gson(); // Создаем Gson для работы с JSON
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0); // Создаем HttpServer
        httpServer.createContext("/tasks", new TaskHandler((InMemoryTaskManager) taskManager, gson));
        httpServer.createContext("/epics", new EpicHandler((InMemoryTaskManager) taskManager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler((InMemoryTaskManager) taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler((InMemoryTaskManager) taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler((InMemoryTaskManager) taskManager, gson));
        httpServer.start(); // Запускаем сервер
        System.out.println("HTTP сервер запущен на порту " + PORT);
    }

    public void start() {
        System.out.println("HTTP сервер запущен на порту " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static Gson getGson() {
        return gson;
    }


    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefoult(); // Получаем экземпляр TaskManager
        HttpTaskServer server = new HttpTaskServer(taskManager);

    }
}
