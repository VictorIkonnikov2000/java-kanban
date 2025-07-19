package manager;

import menegers.FileBackendTaskManager;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackendTaskManagerTest {

    @Test
    void saveAndLoadEmptyFile() throws IOException {
        File file = new File("test_empty.csv");
        file.createNewFile();
        FileBackendTaskManager taskManager = FileBackendTaskManager.loadFromFile(file);
        assertNotNull(taskManager, "Менеджер не должен быть null");
        assertEquals(0, taskManager.getTasks().size(), "Список задач должен быть пуст");
        assertEquals(0, taskManager.getEpics().size(), "Список эпиков должен быть пуст");
        assertEquals(0, taskManager.getSubtasks().size(), "Список подзадач должен быть пуст");
        Files.delete(file.toPath());
    }


}
