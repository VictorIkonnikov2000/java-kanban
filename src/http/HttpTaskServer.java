package http;

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


    public HttpTaskServer(TaskManager manager) throws IOException {
        taskManager = Managers.getDefoult();// Получаем экземпляр TaskManager
        // Создаем Gson для работы с JSON
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0); // Создаем HttpServer
        httpServer.createContext("/tasks", new TaskHandler((InMemoryTaskManager) taskManager));
        httpServer.createContext("/epics", new EpicHandler((InMemoryTaskManager) taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler((InMemoryTaskManager) taskManager));
        httpServer.createContext("/history", new HistoryHandler((InMemoryTaskManager) taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler((InMemoryTaskManager) taskManager));
        httpServer.start(); // Запускаем сервер
        System.out.println("HTTP сервер запущен на порту " + PORT);
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP сервер запущен на порту " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
    }


    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefoult(); // Получаем экземпляр TaskManager
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();
    }
}
