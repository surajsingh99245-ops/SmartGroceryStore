import java.util.List;
import java.util.Scanner;

/**
 * Admin.java
 * Controls the admin-side session: login, and the admin menu loop that
 * wires user input to InventoryManager / SalesManager operations.
 */
public class Admin {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin123";

    private InventoryManager inventoryManager;
    private SalesManager salesManager;
    private Scanner sc;

    public Admin(InventoryManager inventoryManager, SalesManager salesManager, Scanner sc) {
        this.inventoryManager = inventoryManager;
        this.salesManager = salesManager;
        this.sc = sc;
    }

    public boolean login() {
        System.out.print("Username : ");
        String user = sc.nextLine().trim();
        System.out.print("Password : ");
        String pass = sc.nextLine().trim();
        if (user.equals(USERNAME) && pass.equals(PASSWORD)) {
            System.out.println(Main.GREEN + "Login successful." + Main.RESET);
            return true;
        }
        System.out.println(Main.RED + "Invalid username or password." + Main.RESET);
        return false;
    }

    public void showMenu() {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println(Main.CYAN + "\n========== ADMIN PANEL ==========" + Main.RESET);
            System.out.println("1. Add Product");
            System.out.println("2. Update Product");
            System.out.println("3. Delete Product");
            System.out.println("4. Search Product");
            System.out.println("5. View Inventory");
            System.out.println("6. Low Stock Report");
            System.out.println("7. Sales Report");
            System.out.println("8. Restock Product");
            System.out.println("9. Logout");

            int choice = Main.readInt(sc, "Choose Option: ");
            try {
                switch (choice) {
                    case 1: addProductFlow(); break;
                    case 2: updateProductFlow(); break;
                    case 3: deleteProductFlow(); break;
                    case 4: searchProductFlow(); break;
                    case 5: viewInventoryFlow(); break;
                    case 6: lowStockReportFlow(); break;
                    case 7: salesManager.printSalesReport(); break;
                    case 8: restockProductFlow(); break;
                    case 9:
                        System.out.println("Logging out...");
                        loggedIn = false;
                        break;
                    default:
                        System.out.println(Main.RED + "Invalid option. Please choose 1-9." + Main.RESET);
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println(Main.RED + "Error: " + e.getMessage() + Main.RESET);
            } catch (Exception e) {
                System.out.println(Main.RED + "Unexpected error: " + e.getMessage() + Main.RESET);
            }
        }
    }

    private void addProductFlow() {
        System.out.println("\n-- Add Product --");
        int id = Main.readInt(sc, "Product ID : ");

        Product existing = inventoryManager.findById(id);
        if (existing != null) {
            System.out.println(Main.YELLOW + "Product ID " + id + " already exists - updating it instead of adding a new one." + Main.RESET);
            promptAndApplyUpdate(existing);
            return;
        }

        System.out.print("Name : ");
        String name = sc.nextLine().trim();
        System.out.print("Category : ");
        String category = sc.nextLine().trim();
        double price = Main.readDouble(sc, "Price : ");
        int quantity = Main.readInt(sc, "Quantity : ");
        System.out.print("Supplier : ");
        String supplier = sc.nextLine().trim();

        inventoryManager.addProduct(id, name, category, price, quantity, supplier);
        System.out.println(Main.GREEN + "Product Added Successfully." + Main.RESET);
    }

    private void updateProductFlow() {
        System.out.println("\n-- Update Product --");
        int id = Main.readInt(sc, "Enter Product ID : ");
        Product p = inventoryManager.findById(id);
        if (p == null) {
            System.out.println(Main.RED + "Product ID " + id + " not found." + Main.RESET);
            return;
        }
        promptAndApplyUpdate(p);
    }

    /**
     * Shared "press Enter to keep the current value" field-collection flow.
     * Used both by Update Product and by Add Product when the entered ID
     * turns out to already exist (see addProductFlow above).
     */
    private void promptAndApplyUpdate(Product p) {
        System.out.println("Existing details:\n" + p);
        System.out.println("Press Enter to keep the current value for any field.");

        System.out.print("New Name [" + p.getName() + "] : ");
        String name = sc.nextLine().trim();

        System.out.print("New Category [" + p.getCategory() + "] : ");
        String category = sc.nextLine().trim();

        System.out.print("New Price [" + String.format("%.2f", p.getPrice()) + "] : ");
        String priceStr = sc.nextLine().trim();
        Double price = priceStr.isEmpty() ? null : Double.parseDouble(priceStr);

        System.out.print("New Quantity [" + p.getQuantity() + "] : ");
        String qtyStr = sc.nextLine().trim();
        Integer quantity = qtyStr.isEmpty() ? null : Integer.parseInt(qtyStr);

        System.out.print("New Supplier [" + p.getSupplier() + "] : ");
        String supplier = sc.nextLine().trim();

        inventoryManager.updateProduct(p.getId(), name, category, price, quantity, supplier);
        System.out.println(Main.GREEN + "Product Updated Successfully." + Main.RESET);
    }

    private void deleteProductFlow() {
        System.out.println("\n-- Delete Product --");
        int id = Main.readInt(sc, "Enter Product ID : ");
        Product p = inventoryManager.findById(id);
        if (p == null) {
            System.out.println(Main.RED + "Product ID " + id + " not found." + Main.RESET);
            return;
        }
        System.out.println(p);
        System.out.print("Are you sure you want to delete this product? (yes/no): ");
        String confirm = sc.nextLine().trim();
        if (confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y")) {
            inventoryManager.deleteProduct(id);
            System.out.println(Main.GREEN + "Product deleted successfully." + Main.RESET);
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void searchProductFlow() {
        System.out.println("\n-- Search Product --");
        System.out.println("1. By Product ID");
        System.out.println("2. By Product Name");
        System.out.println("3. By Category");
        int choice = Main.readInt(sc, "Choose Option : ");
        switch (choice) {
            case 1: {
                int id = Main.readInt(sc, "Enter Product ID : ");
                Product p = inventoryManager.findById(id);
                if (p == null) {
                    System.out.println(Main.RED + "Product not found." + Main.RESET);
                } else {
                    System.out.println(p);
                }
                break;
            }
            case 2: {
                System.out.print("Enter Product Name : ");
                String name = sc.nextLine().trim();
                List<Product> matches = inventoryManager.findByNamePart(name);
                inventoryManager.printTable(matches);
                break;
            }
            case 3: {
                System.out.print("Enter Category : ");
                String category = sc.nextLine().trim();
                List<Product> matches = inventoryManager.findByCategory(category);
                inventoryManager.printTable(matches);
                break;
            }
            default:
                System.out.println(Main.RED + "Invalid option." + Main.RESET);
        }
    }

    private void viewInventoryFlow() {
        System.out.println("\n-- View Inventory --");
        System.out.println("1. Sort by Name (default)");
        System.out.println("2. Sort by Price");
        int choice = Main.readInt(sc, "Choose Option : ");
        List<Product> list = (choice == 2) ? inventoryManager.getSortedByPrice() : inventoryManager.getSortedByName();
        inventoryManager.printTable(list);
    }

    private void lowStockReportFlow() {
        System.out.println(Main.YELLOW + "\nLOW STOCK ALERT" + Main.RESET);
        List<Product> low = inventoryManager.getLowStock();
        if (low.isEmpty()) {
            System.out.println("No products are low on stock.");
        } else {
            for (Product p : low) {
                System.out.println(p.getName() + " (Qty: " + p.getQuantity() + ")");
            }
        }

        System.out.println(Main.RED + "\nOUT OF STOCK" + Main.RESET);
        List<Product> out = inventoryManager.getOutOfStock();
        if (out.isEmpty()) {
            System.out.println("No products are out of stock.");
        } else {
            for (Product p : out) {
                System.out.println(p.getName());
            }
        }
    }

    private void restockProductFlow() {
        System.out.println("\n-- Restock Product --");
        int id = Main.readInt(sc, "Enter Product ID : ");
        Product p = inventoryManager.findById(id);
        if (p == null) {
            System.out.println(Main.RED + "Product ID " + id + " not found." + Main.RESET);
            return;
        }
        System.out.println("Current Stock : " + p.getQuantity());
        int addQty = Main.readInt(sc, "Enter New Stock : ");
        inventoryManager.restock(id, addQty);
        System.out.println("Updated Stock : " + p.getQuantity());
    }
}