package Manager;

import Tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    static final int MAX_SIZE_HISTORY = 10;

    private final List<Task> listTaskHistory;

    public InMemoryHistoryManager() {
        listTaskHistory = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        listTaskHistory.add(task);
        if (listTaskHistory.size() > MAX_SIZE_HISTORY) {
            listTaskHistory.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(listTaskHistory);
    }
}
