package http;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


import menegers.InMemoryTaskManager;
import menegers.Managers;



import static org.junit.jupiter.api.Assertions.assertEquals;


class HttpTaskServerTest {

    private HttpTaskServer server;
    private HttpClient client;
    private InMemoryTaskManager taskManager;
    private URI baseUri;


    @BeforeEach
    void setUp() throws IOException {
        taskManager = (InMemoryTaskManager) Managers.getDefoult();
        server = new HttpTaskServer(taskManager);
        baseUri = URI.create("http://localhost:8080");
        client = HttpClient.newHttpClient();

    }

    @AfterEach
    void tearDown() {
        server.stop();
    }


    @Test
    void shouldReturn200OnGetTasks() throws IOException, InterruptedException {
        URI uri = baseUri.resolve("/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса должен быть 200");
    }

    @Test
    void shouldReturn405OnPostTasks() throws IOException, InterruptedException {
        URI uri = baseUri.resolve("/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode(), "Код статуса должен быть 405");
    }



}
