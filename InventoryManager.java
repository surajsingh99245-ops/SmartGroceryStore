import java.util.*;

/**
 * InventoryManager.java
 * Owns the master list of products and every operation that reads or
 * mutates inventory. Persists to disk (via FileManager) after every
 * change so the app can be closed and reopened without losing data.
 */
public class InventoryManager {

    public static final int LOW_STOCK_THRESHOLD = 10;

    private List<Product> products;
    private FileManager fileManager;

    public InventoryManager() {
        this.fileManager = new FileManager();
        boolean trueFirstRun = !fileManager.productsFileExists();
        this.products = fileManager.loadProducts();
        if (trueFirstRun) {
            seedDummyData();
            persist();
        }
    }

    /**
     * Populates a starter catalog so the app is demo-ready immediately after
     * download instead of showing an empty store. Only runs once, the very
     * first time products.txt doesn't exist yet - if an admin later deletes
     * every product on purpose, the store is allowed to stay empty (deleting
     * products.txt itself is the "reset to demo data" action).
     */
    private void seedDummyData() {
        products.add(new Product(101, "Rice", "Grocery", 60, 100, "ABC Traders"));
        products.add(new Product(102, "Milk", "Dairy", 30, 40, "XYZ Dairy"));
        products.add(new Product(103, "Bread", "Bakery", 35, 25, "Local Bakery"));
        products.add(new Product(104, "Eggs", "Dairy", 70, 8, "XYZ Dairy"));
        products.add(new Product(105, "Sugar", "Grocery", 45, 60, "ABC Traders"));
        products.add(new Product(106, "Salt", "Grocery", 20, 5, "ABC Traders"));
        products.add(new Product(107, "Cooking Oil", "Grocery", 150, 30, "Sunrise Oils"));
        products.add(new Product(108, "Butter", "Dairy", 55, 0, "XYZ Dairy"));
        products.add(new Product(109, "Tea", "Beverages", 120, 20, "Chai Traders"));
        products.add(new Product(110, "Soap", "Household", 25, 0, "Clean Co"));
        products.add(new Product(111, "Shampoo", "Household", 90, 15, "Clean Co"));
        products.add(new Product(112, "Biscuits", "Snacks", 15, 100, "Tasty Foods"));
    }

    private void persist() {
        fileManager.saveProducts(products);
    }

    public boolean isIdTaken(int id) {
        return findById(id) != null;
    }

    public Product findById(int id) {
        for (Product p : products) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public List<Product> findByNamePart(String namePart) {
        List<Product> matches = new ArrayList<>();
        String needle = namePart.toLowerCase();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(needle)) {
                matches.add(p);
            }
        }
        return matches;
    }

    public List<Product> findByCategory(String category) {
        List<Product> matches = new ArrayList<>();
        String needle = category.toLowerCase();
        for (Product p : products) {
            if (p.getCategory().toLowerCase().contains(needle)) {
                matches.add(p);
            }
        }
        return matches;
    }

    /**
     * Adds a new product. Throws IllegalArgumentException for any validation
     * failure so the caller (Admin) can show a clean message instead of the
     * app crashing.
     */
    public void addProduct(int id, String name, String category, double price, int quantity, String supplier) {
        if (isIdTaken(id)) {
            throw new IllegalArgumentException("A product with ID " + id + " already exists.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        products.add(new Product(id, name.trim(), category.trim(), price, quantity, supplier.trim()));
        persist();
    }

    /**
     * Updates only the fields that are non-null. Pass null for a String
     * field or Double/Integer field to leave it unchanged.
     */
    public void updateProduct(int id, String name, String category, Double price, Integer quantity, String supplier) {
        Product p = findById(id);
        if (p == null) {
            throw new IllegalArgumentException("Product ID " + id + " not found.");
        }
        if (name != null && !name.trim().isEmpty()) {
            p.setName(name.trim());
        }
        if (category != null && !category.trim().isEmpty()) {
            p.setCategory(category.trim());
        }
        if (price != null) {
            if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
            p.setPrice(price);
        }
        if (quantity != null) {
            if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative.");
            p.setQuantity(quantity);
        }
        if (supplier != null && !supplier.trim().isEmpty()) {
            p.setSupplier(supplier.trim());
        }
        persist();
    }

    public void deleteProduct(int id) {
        Product p = findById(id);
        if (p == null) {
            throw new IllegalArgumentException("Product ID " + id + " not found.");
        }
        products.remove(p);
        persist();
    }

    public void restock(int id, int additionalQuantity) {
        Product p = findById(id);
        if (p == null) {
            throw new IllegalArgumentException("Product ID " + id + " not found.");
        }
        if (additionalQuantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be greater than zero.");
        }
        p.increaseQuantity(additionalQuantity);
        persist();
    }

    /** Reduces stock at checkout time. Called only by BillingSystem. */
    public void reduceStockForSale(int id, int amount) {
        Product p = findById(id);
        if (p != null) {
            p.decreaseQuantity(amount);
        }
        persist();
    }

    public List<Product> getSortedByName() {
        List<Product> sorted = new ArrayList<>(products);
        sorted.sort(Comparator.comparing(p -> p.getName().toLowerCase()));
        return sorted;
    }

    public List<Product> getSortedByPrice() {
        List<Product> sorted = new ArrayList<>(products);
        sorted.sort(Comparator.comparingDouble(Product::getPrice));
        return sorted;
    }

    public List<Product> getLowStock() {
        List<Product> low = new ArrayList<>();
        for (Product p : products) {
            if (p.isLowStock()) low.add(p);
        }
        return low;
    }

    public List<Product> getOutOfStock() {
        List<Product> out = new ArrayList<>();
        for (Product p : products) {
            if (p.isOutOfStock()) out.add(p);
        }
        return out;
    }

    public List<Product> getAll() {
        return products;
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }

    /** Prints a list of products as an aligned table. Shared by admin + customer views. */
    public void printTable(List<Product> list) {
        if (list.isEmpty()) {
            System.out.println("No products to display.");
            return;
        }
        System.out.println("--------------------------------------------------------------");
        System.out.printf("%-6s%-16s%-14s%-10s%-8s%-15s%n", "ID", "Name", "Category", "Price", "Qty", "Status");
        System.out.println("--------------------------------------------------------------");
        for (Product p : list) {
            String status = p.isOutOfStock() ? "OUT OF STOCK" : (p.isLowStock() ? "LOW STOCK" : "In Stock");
            System.out.printf("%-6d%-16s%-14s%-10.2f%-8d%-15s%n",
                    p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity(), status);
        }
        System.out.println("--------------------------------------------------------------");
    }
}