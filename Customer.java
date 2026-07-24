import java.util.Scanner;

/**
 * Customer.java
 * Controls the customer-side session: the customer menu loop that wires
 * user input to BillingSystem operations (view, buy, cart, checkout).
 */
public class Customer {

    private BillingSystem billingSystem;
    private Scanner sc;

    public Customer(BillingSystem billingSystem, Scanner sc) {
        this.billingSystem = billingSystem;
        this.sc = sc;
    }

    public void showMenu() {
        boolean browsing = true;
        while (browsing) {
            System.out.println(Main.CYAN + "\n========== CUSTOMER ==========" + Main.RESET);
            System.out.println("1. View Products");
            System.out.println("2. Buy Product");
            System.out.println("3. View Cart");
            System.out.println("4. Remove Product");
            System.out.println("5. Checkout");
            System.out.println("6. Back");

            int choice = Main.readInt(sc, "Choose Option: ");
            try {
                switch (choice) {
                    case 1: billingSystem.viewProducts(); break;
                    case 2: buyProductFlow(); break;
                    case 3: billingSystem.viewCart(); break;
                    case 4: removeProductFlow(); break;
                    case 5: checkoutFlow(); break;
                    case 6:
                        System.out.println("Returning to main menu...");
                        browsing = false;
                        break;
                    default:
                        System.out.println(Main.RED + "Invalid option. Please choose 1-6." + Main.RESET);
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println(Main.RED + "Error: " + e.getMessage() + Main.RESET);
            } catch (Exception e) {
                System.out.println(Main.RED + "Unexpected error: " + e.getMessage() + Main.RESET);
            }
        }
    }

    private void buyProductFlow() {
        int id = Main.readInt(sc, "Product ID : ");
        int quantity = Main.readInt(sc, "Quantity : ");
        billingSystem.buyProduct(id, quantity);
    }

    private void removeProductFlow() {
        int id = Main.readInt(sc, "Enter Product ID to remove : ");
        billingSystem.removeProduct(id);
    }

    private void checkoutFlow() {
        if (billingSystem.isCartEmpty()) {
            System.out.println(Main.RED + "Your cart is empty. Add products before checking out." + Main.RESET);
            return;
        }
        System.out.print("Enter coupon code (or press Enter to skip): ");
        String coupon = sc.nextLine().trim();
        String receipt = billingSystem.checkout(coupon.isEmpty() ? null : coupon);
        System.out.println("\n" + receipt);
    }
}
