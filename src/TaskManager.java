import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class TaskManager {

    private static final String DATA_FILE = "tasks.json";
    private static final List<Task> tasks = new ArrayList<>();

    public static void main(String[] args){
        loadTasks();

        Scanner scanner = new Scanner(System.in);

        System.out.println("==Task Manager===");

        boolean running = true;
        while(running){
            printMenu();
            System.out.println("What would you like to do? ");
            String choice = scanner.nextLine().toLowerCase().trim();

            try {
                switch(choice){
                    case "1":
                        addTask(scanner);
                        break;
                    case "2":
                        updateTask(scanner);
                        break;
                    case "3":
                        deleteTask(scanner);
                        break;
                    case "4":
                        listTasks();
                        break;
                    case "5":
                        markTaskComplete(scanner, true);
                        break;
                    case "6":
                        markTaskComplete(scanner, false);
                        break;
                    case "7":
                        saveTasks();
                        System.out.println("Tasks Saved!");
                        break;
                    case "exit":
                        running = false;
                        System.out.println("App closed!");
                        break;
                    default:
                        System.out.println("Invalid! Unavailable option. Try again..");
                        break;
                }
            }catch(Exception e){
                System.err.println("Unexpected error occurred: " + e.getMessage());
            }
        }
    }
}
