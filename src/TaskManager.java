import com.sun.security.jgss.GSSUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskManager {

    private static final String DATA_FILE = "tasks.json";
    private static final List<Task> tasks = new ArrayList<>();

    public static void main(String[] args){
        loadTasks();

        Scanner scanner = new Scanner(System.in);

        System.out.println("===Task Manager===");

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
        tasks.add(task);
        saveTasks();
        System.out.println("Task added successfully");
    }

    private static void updateTask(Scanner scanner) {
        if (tasks.isEmpty()) {
            System.out.println("No task to update");
            return;
        }

        listTasks();
        int id = promptForTaskId(scanner, "Enter task ID needing update");
        Task task = findTaskById(id);

        if(task == null){
            System.out.println("Task with ID: " +  id + " Not Found!");
            // return;
        }

        System.out.print("New title (leave blank to keep '" + task.title + "'): ");
        String title = scanner.nextLine().trim();
        if(!title.isEmpty()){
            task.title = title;
        }

        System.out.print("New description(leave blank to keep current description): ");
        String description = scanner.nextLine().trim();
        if (!description.isEmpty()){
            task.description = description;
        }

        saveTasks();
        System.out.println("Task updated successfully.");


    }

    private static void deleteTask(Scanner scanner){
        if(tasks.isEmpty()){
            System.out.println("There are no tasks to delete");
            return;
        }

        listTasks();
        int id = promptForTaskId(scanner, "Enter task ID to be deleted: ");
        Task task = findTaskById(id);

        if (task == null){
            System.out.println("Task with ID " + id + " does not exist.");
            return;
        }

        tasks.remove(task);
        saveTasks();
        System.out.println("Task deleted successfully");
    }

    private static void listTasks(){
        if (tasks.isEmpty()){
            System.out.println("No tasks available.");
            return;
        }

        System.out.println("\nAvailable Tasks: ");
        for (Task task : tasks){
            String status = task.completed ? "[Done" : "[Pending";
            System.out.println(status + " ID: " + task.id + " | " + task.title);
            if(!task.description.isEmpty()){
                System.out.println("   Description: "+ task.description);
            }
        }
    }

    private static void markTaskComplete(Scanner scanner, boolean complete){
        if(tasks.isEmpty()){
            System.out.println("There are no tasks to update.");
            return;
        }

        listTasks();
        int id = promptForTaskId((Scanner scanner, "Enter task ID: ");
        Task task = findTaskById(id);

        if(task == null){
            System.out.println("Task with ID " + id + " was not found");
            return;
        }

        task.completed = complete;
        saveTasks();
        String message = complete ? "marked as complete." : "marked as incomplete.";
        System.out.println("Task " + id + " " + message);
    }

    private static int promptForTaskId(Scanner scanner, String prompt){
        while(true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try{
                int id = Integer.parseInt(raw);
                if(id <= 0){
                    System.out.println("ID must be a positive number.");
                    continue;
                }
                return id;
            } catch (NumberFormatException e){
                System.out.println("Invalid ID: Please enter a valid positive integer.");
            }
        }
    }

    private static int nextId(){
        int maxId = 0;
        for (Task task : tasks){
            if(task.id > maxId){
                maxId = task.id;
            }
        }
        return maxId + 1;
    }

    private static Task findTaskById(int id){
        for (Task task : tasks){
            if (task.id == id){
                return task;
            }
        }
        return null;
    }

    private static void loadTasks(){
        Path filePath = Path.of(DATA_FILE);
        if(!Files.exists(filePath)){
            return;
        }

        try{
            String content = Files.readString(filePath, StandardCharsets.UTF_8);
            if (content == null || content.trim().isEmpty()){
                return;
            }

            Pattern pattern = Pattern.compile("\\{\\s*\"id\"\\s*:\\s*(\\d+)\\s*,\\s*\"title\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"\\s*,\\s*\"description\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"\\s*,\\s*\"completed\"\\s*:\\s*(true|false)\\s*\\}");
            Matcher matcher = pattern.matcher(content);
            tasks.clear();

            while(matcher.find()){
                int id = Integer.parseInt((matcher.group(1)));
                String title = unescapeJson((matcher.group(2)));
                String description = unescapeJson((matcher.group(3)));
                boolean completed = Boolean.parseBoolean(matcher.group(4));
                tasks.add(new Task(id, title, description, completed));
            }

            System.out.println("Loaded " + tasks.size() + " task(s) from " + DATA_FILE + ".");
        } catch (IOException | IllegalArgumentException e){
            System.err.println("Warning: not able to load tasks from " + DATA_FILE + ". Starting with empty list.");
            tasks.clear();
        }
    }

    private static void saveTasks(){}

    private static String escapeJson(String value){}

    private static String unescapeJson(String value){}

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
