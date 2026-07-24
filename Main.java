import java.util.Scanner;

/**
 * Main.java
 * Application entry point. Owns the top-level menu loop and a handful
 * of small static helpers (safe numeric input, ANSI color constants)
 * that are shared across Admin and Customer without needing a
 * dedicated utility class.
 */
public class Main {

    // ---- ANSI color helpers (bonus: colorful terminal output) ----
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        InventoryManager inventoryManager = new InventoryManager();
        SalesManager salesManager = new SalesManager();
        BillingSystem billingSystem = new BillingSystem(inventoryManager, salesManager);
        Admin admin = new Admin(inventoryManager, salesManager, sc);
        Customer customer = new Customer(billingSystem, sc);

        boolean running = true;
        while (running) {
            System.out.println(CYAN + "\n==============================" + RESET);
            System.out.println(" SMART GROCERY STORE");
            System.out.println(CYAN + "==============================" + RESET);
            System.out.println("1. Admin Login");
            System.out.println("2. Customer Billing");
            System.out.println("3. Exit");

            int choice = readInt(sc, "\nChoose Option: ");
            switch (choice) {
                case 1:
                    if (admin.login()) {
                        admin.showMenu();
                    }
                    break;
                case 2:
                    customer.showMenu();
                    break;
                case 3:
                    System.out.println(GREEN + "\nThank you for visiting Smart Grocery Store. Goodbye!" + RESET);
                    running = false;
                    break;
                default:
                    System.out.println(RED + "Invalid option. Please choose 1-3." + RESET);
            }
        }
        sc.close();
    }

    /** Reads a line and parses it as an int, re-prompting on bad input. Never throws. */
    public static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid input. Please enter a whole number." + RESET);
            }
        }
    }

    /** Reads a line and parses it as a double, re-prompting on bad input. Never throws. */
    public static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid input. Please enter a valid number." + RESET);
            }
        }
    }
}
