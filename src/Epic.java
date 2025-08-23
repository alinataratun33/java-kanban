import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subTaskIds;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.subTaskIds = new ArrayList<>();
    }


    public ArrayList<Integer> getSubTaskIds() {
        return new ArrayList<>(subTaskIds);
    }

    public void deleteSubTask(Integer subTaskId) {
        if (!subTaskIds.contains(subTaskId)) {
            return;
        }
        subTaskIds.remove(subTaskId);
    }


    public void clearSubTasks() {
        subTaskIds.clear();
    }


    public void addSubTaskId(int subTaskId) {
        if (subTaskId == getId()) {
            return;
        }
        if (!subTaskIds.contains(subTaskId)) {
            subTaskIds.add(subTaskId);
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                "description='" + getDescription() + '\'' +
                "id='" + getId() + '\'' +
                "status='" + status + '\'' +
                "subTask='" + subTaskIds + '\'' +
                '}';
    }
}
