package manager;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private static final String TEST_FILE_PATH = "test_tasks.csv";
    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(new File(TEST_FILE_PATH));
    }

    @Test
    public void testSaveToFileWithInvalidPath() {
        FileBackedTaskManager manager = new FileBackedTaskManager(new File("/invalid_directory/" + TEST_FILE_PATH));
        assertThrows(ManagerSaveException.class, () -> {
            manager.save();
        });
    }
}
