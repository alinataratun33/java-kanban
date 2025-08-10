import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        Manager manager = new Manager();

        Epic epic1 = manager.createEpic(new Epic("fsf", " e", Status.NEW));


        ArrayList<Task> task = manager.getAllTasks();

        System.out.println(task);


    }
}
