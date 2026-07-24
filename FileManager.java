import java.io.*;
import java.util.*;

/**
 * FileManager.java
 * Centralizes all file reading/writing so the rest of the app never
 * touches java.io directly. Every method fails "softly" - if a file is
 * missing or unreadable it simply returns an empty result rather than
 * throwing, since a brand-new run of the app (with no data files yet)
 * must still work correctly.
 */
public class FileManager {

    public static final String PRODUCTS_FILE = "products.txt";
    public static final String SALES_FILE = "sales.txt";
    public static final String BILL_HISTORY_FILE = "bill_history.txt";
    public static final String BILLS_FOLDER = "Bills";

    /** True once products.txt has been created at least once (used to detect a true first run). */
    public boolean productsFileExists() {
        return new File(PRODUCTS_FILE).exists();
    }

    /** Loads every product from products.txt. Returns an empty list on first run. */
    public List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        for (String line : readAllLines(PRODUCTS_FILE)) {
            try {
                products.add(Product.fromFileLine(line));
            } catch (IllegalArgumentException e) {
                System.out.println("Skipping corrupted product record: " + e.getMessage());
            }
        }
        return products;
    }

    /** Overwrites products.txt with the current in-memory product list. */
    public void saveProducts(List<Product> products) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product p : products) {
                writer.println(p.toFileLine());
            }
        } catch (IOException e) {
            System.out.println("Warning: could not save products (" + e.getMessage() + ")");
        }
    }

    /** Appends one sale line-item record to sales.txt. */
    public void appendSaleRecord(String line) {
        appendLine(SALES_FILE, line);
    }

    /** Reads back every sale line-item ever recorded. */
    public List<String> loadSaleRecords() {
        return readAllLines(SALES_FILE);
    }

    /** Appends a one-line bill summary to bill_history.txt. */
    public void appendBillHistory(String line) {
        appendLine(BILL_HISTORY_FILE, line);
    }

    /** Reads back every bill summary ever recorded. */
    public List<String> loadBillHistoryLines() {
        return readAllLines(BILL_HISTORY_FILE);
    }

    /** How many bills have been recorded so far - used to number the next bill. */
    public int countBillHistoryEntries() {
        return loadBillHistoryLines().size();
    }

    /** Writes a full formatted receipt into Bills/&lt;billNumber&gt;.txt */
    public void exportBillToFile(String billNumber, String receiptText) {
        File folder = new File(BILLS_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File billFile = new File(folder, billNumber + ".txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(billFile))) {
            writer.print(receiptText);
        } catch (IOException e) {
            System.out.println("Warning: could not export bill file (" + e.getMessage() + ")");
        }
    }

    // ----- small shared helpers -----

    private void appendLine(String filename, String line) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            writer.println(line);
        } catch (IOException e) {
            System.out.println("Warning: could not write to " + filename + " (" + e.getMessage() + ")");
        }
    }

    private List<String> readAllLines(String filename) {
        List<String> lines = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) {
            return lines;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read " + filename + " (" + e.getMessage() + ")");
        }
        return lines;
    }
}