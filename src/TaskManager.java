import com.sun.security.jgss.GSSUtil;

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
        scanner.close();
    }

    private static void printMenu(){
        System.out.println();
        System.out.println("1. Add a task");
        System.out.println("2. Update task");
        System.out.println("3. Delete task");
        System.out.println("4. List tasks");
        System.out.println("5. Mark task completed");
        System.out.println("6. Mark task uncompleted");
        System.out.println("7. Save tasks");
        System.out.println("8. Exit App");
    }

    private static void addTask(Scanner scanner){
        System.out.println("Title");
        String title = scanner.nextLine().trim();

        if (title.isEmpty()){
            System.out.println("Task title cannot be empty");
            return;
        }

        System.out.println("Description (optional)");
        String description = scanner.nextLine().trim();

        Task task = new Task(nextId(), title, description, false);
        task.add(task);
        saveTasks();
        System.out.println("Task added successfully");
    }

    private static void updateTask(Scanner scanner){}

    private static void deleteTask(Scanner scanner){}

    private static void listTasks(){}

    private static void markTaskComplete(Scanner scanner, boolean complete){}

    private static int promptForTaskId(Scanner scanner, String prompt){}

    private static int nextId(){}

    private static Task findTaskById(int id){}

    private static void loadTasks(){}

    private static void saveTasks(){}

    private static String escapeJson(String value){}

    private static String unescapeJscon(String value){}

    private static class Task {
        private int id;
        private String title;
        private String description;
        private boolean completed;

        private Task(int id, String title, String description, boolean completed) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.completed = completed;
        }
    }
}
