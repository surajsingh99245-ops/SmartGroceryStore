/**
 * Product.java
 * Represents a single grocery product in the store inventory.
 * Demonstrates encapsulation: every field is private with public getters/setters.
 */
public class Product {

    private int id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private String supplier;

    public Product(int id, String name, String category, double price, int quantity, String supplier) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.supplier = supplier;
    }

    // ----- Getters -----
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSupplier() {
        return supplier;
    }

    // ----- Setters -----
    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    // ----- Convenience helpers used by InventoryManager / BillingSystem -----

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        this.quantity -= amount;
    }

    public boolean isOutOfStock() {
        return quantity == 0;
    }

    public boolean isLowStock() {
        return quantity > 0 && quantity < 10;
    }

    /**
     * Serializes this product into a single line for storage in products.txt.
     * Uses '|' as a delimiter (instead of ',') since product names could
     * legitimately contain commas.
     */
    public String toFileLine() {
        return id + "|" + name + "|" + category + "|" + price + "|" + quantity + "|" + supplier;
    }

    /**
     * Parses a Product back out of a line previously written by toFileLine().
     * Throws IllegalArgumentException on a malformed line so the caller
     * (FileManager) can skip/report the bad line instead of the app crashing.
     */
    public static Product fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 6) {
            throw new IllegalArgumentException("Malformed product line: " + line);
        }
        int id = Integer.parseInt(parts[0].trim());
        String name = parts[1].trim();
        String category = parts[2].trim();
        double price = Double.parseDouble(parts[3].trim());
        int quantity = Integer.parseInt(parts[4].trim());
        String supplier = parts[5].trim();
        return new Product(id, name, category, price, quantity, supplier);
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %d%nName: %s%nCategory: %s%nPrice: %.2f%nQuantity: %d%nSupplier: %s",
                id, name, category, price, quantity, supplier);
    }
}
