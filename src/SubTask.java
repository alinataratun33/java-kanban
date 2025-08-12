public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + getName() + '\'' +
                "description='" + getDescription() + '\'' +
                "id='" + getId() + '\'' +
                "epicId='" + epicId + '\'' +
                "status='" + status + '\'' +
                '}';
    }
}
