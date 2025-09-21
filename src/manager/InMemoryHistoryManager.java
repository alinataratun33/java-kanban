package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    // private final List<Task> listTaskHistory;
    private final Map<Integer, Node> mapNode;
    private Node tail;
    private Node head;

    private int size = 0;


    public InMemoryHistoryManager() {
        mapNode = new HashMap<>();

        // listTaskHistory = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        linkLast(task);
    }


    private void linkLast(Task task) {
        if (mapNode.containsKey(task.getId())) {
            removeNode(mapNode.get(task.getId()));
        }

        Node oldTail = tail;
        Node newNode = new Node(task, null, oldTail);
        tail = newNode;

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        mapNode.put(task.getId(), newNode);
        size++;
    }

    @Override
    public void remove(int id) {
        if (mapNode.get(id) != null) {
            removeNode(mapNode.get(id));
        }
    }


    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        mapNode.remove(node.task.getId());

        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }

        if (node.next == null) tail = node.prev;
        else {
            node.next.prev = node.prev;
        }
        node.prev = null;
        node.next = null;
        node.task = null;
        size--;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;

        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
