package manager;

public class Managers {

    private static final InMemoryHistoryManager history = new InMemoryHistoryManager();


    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return history;
    }
}
