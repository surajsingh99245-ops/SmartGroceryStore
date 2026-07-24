/**
 * CartItem.java
 * Represents one line item inside a customer's shopping cart.
 * Stores a snapshot of the product's name and price at the time it was
 * added, so the cart stays consistent even if inventory changes elsewhere
 * before checkout.
 */
public class CartItem {

    private int productId;
    private String productName;
    private double price;
    private int quantity;

    public CartItem(int productId, String productName, double price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    public double getTotal() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return String.format("%s%n%d x %.2f = %.2f", productName, quantity, price, getTotal());
    }
}
