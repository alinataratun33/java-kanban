package Manager;

import Manager.HistoryManager;
import Manager.InMemoryHistoryManager;
import Manager.InMemoryTaskManager;

public class Managers {

    private final static InMemoryHistoryManager history = new InMemoryHistoryManager();


    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return history;
    }
}
