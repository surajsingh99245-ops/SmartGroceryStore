import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * BillingSystem.java
 * Handles the customer-facing shopping cart and checkout process. Talks
 * to InventoryManager to validate/adjust stock and to SalesManager to
 * record completed sales and export receipts.
 */
public class BillingSystem {

    private static final double GST_RATE = 0.05;        // 5% GST
    private static final double COUPON_DISCOUNT = 0.10; // SAVE10 -> 10% off
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private List<CartItem> cart;
    private InventoryManager inventoryManager;
    private SalesManager salesManager;

    public BillingSystem(InventoryManager inventoryManager, SalesManager salesManager) {
        this.inventoryManager = inventoryManager;
        this.salesManager = salesManager;
        this.cart = new ArrayList<>();
    }

    public void viewProducts() {
        inventoryManager.printTable(inventoryManager.getSortedByName());
    }

    /** How many units of this product are already sitting in the cart. */
    private int quantityAlreadyInCart(int productId) {
        for (CartItem item : cart) {
            if (item.getProductId() == productId) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    public void buyProduct(int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        Product product = inventoryManager.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found.");
        }
        int alreadyReserved = quantityAlreadyInCart(productId);
        int availableToReserve = product.getQuantity() - alreadyReserved;
        if (quantity > availableToReserve) {
            throw new IllegalArgumentException("Not enough stock.");
        }
        for (CartItem item : cart) {
            if (item.getProductId() == productId) {
                item.addQuantity(quantity);
                System.out.println("Cart updated: " + item.getProductName() + " x " + item.getQuantity());
                return;
            }
        }
        cart.add(new CartItem(product.getId(), product.getName(), product.getPrice(), quantity));
        System.out.println("Added to cart: " + product.getName() + " x " + quantity);
    }

    public void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        System.out.println("--------------------------------------------------------------");
        System.out.printf("%-20s%-10s%-10s%-10s%n", "Product", "Price", "Qty", "Total");
        System.out.println("--------------------------------------------------------------");
        double runningTotal = 0;
        for (CartItem item : cart) {
            System.out.printf("%-20s%-10.2f%-10d%-10.2f%n",
                    item.getProductName(), item.getPrice(), item.getQuantity(), item.getTotal());
            runningTotal += item.getTotal();
        }
        System.out.println("--------------------------------------------------------------");
        System.out.printf("Cart Total: %.2f%n", runningTotal);
    }

    public void removeProduct(int productId) {
        CartItem toRemove = null;
        for (CartItem item : cart) {
            if (item.getProductId() == productId) {
                toRemove = item;
                break;
            }
        }
        if (toRemove == null) {
            throw new IllegalArgumentException("That product is not in your cart.");
        }
        cart.remove(toRemove);
        System.out.println("Removed " + toRemove.getProductName() + " from cart.");
    }

    public boolean isCartEmpty() {
        return cart.isEmpty();
    }

    /**
     * Finalizes the sale: decrements inventory, calculates totals (with an
     * optional SAVE10 coupon + 5% GST), writes the bill to bill_history.txt
     * and Bills/&lt;billNumber&gt;.txt, logs each line item to sales.txt,
     * then empties the cart. Returns the formatted receipt text.
     */
    public String checkout(String couponCode) {
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cart is empty. Add products before checking out.");
        }

        double subtotal = 0;
        for (CartItem item : cart) {
            subtotal += item.getTotal();
        }

        boolean couponProvided = couponCode != null && !couponCode.trim().isEmpty();
        boolean couponApplied = couponProvided && couponCode.trim().equalsIgnoreCase("SAVE10");
        if (couponProvided && !couponApplied) {
            System.out.println(Main.YELLOW + "Coupon code not recognized. Proceeding without discount." + Main.RESET);
        }

        double discount = couponApplied ? subtotal * COUPON_DISCOUNT : 0;
        double discountedSubtotal = subtotal - discount;
        double gst = discountedSubtotal * GST_RATE;
        double grandTotal = discountedSubtotal + gst;

        String billNumber = salesManager.generateNextBillNumber();
        String dateTime = LocalDateTime.now().format(DATETIME_FMT);

        StringBuilder receipt = new StringBuilder();
        receipt.append("========================\n");
        receipt.append("   SMART GROCERY STORE\n");
        receipt.append("========================\n");
        receipt.append("Bill No : ").append(billNumber).append("\n");
        receipt.append("Date    : ").append(dateTime).append("\n\n");

        for (CartItem item : cart) {
            receipt.append(item.getProductName()).append("\n");
            receipt.append(item.getQuantity()).append(" x ").append(String.format("%.2f", item.getPrice()))
                   .append(" = ").append(String.format("%.2f", item.getTotal())).append("\n\n");
        }

        receipt.append("------------------------\n");
        receipt.append(String.format("Subtotal : %.2f%n", subtotal));
        if (couponApplied) {
            receipt.append(String.format("Discount (SAVE10) : -%.2f%n", discount));
        }
        receipt.append(String.format("GST (5%%) : %.2f%n", gst));
        receipt.append(String.format("Grand Total : %.2f%n", grandTotal));
        receipt.append("\nThank You\nVisit Again\n");

        // Persist: reduce stock, log each sold item, save bill history + export receipt file
        for (CartItem item : cart) {
            inventoryManager.reduceStockForSale(item.getProductId(), item.getQuantity());
            salesManager.recordSaleLineItem(billNumber, item.getProductName(), item.getQuantity(), item.getPrice(), item.getTotal());
        }
        salesManager.recordBillSummary(billNumber, subtotal, discount, gst, grandTotal, cart.size());
        salesManager.exportReceipt(billNumber, receipt.toString());

        cart.clear();
        return receipt.toString();
    }
}
