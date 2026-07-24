import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * SalesManager.java
 * Records every completed sale (per line item, and per bill summary) and
 * produces the admin-facing sales report. All persistence is delegated
 * to FileManager; this class only knows how to aggregate the data once
 * it has been read back.
 */
public class SalesManager {

    private FileManager fileManager;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SalesManager() {
        this.fileManager = new FileManager();
    }

    public String generateNextBillNumber() {
        int next = fileManager.countBillHistoryEntries() + 1;
        return String.format("BILL%04d", next);
    }

    /** Logs one product line-item of a completed sale to sales.txt. */
    public void recordSaleLineItem(String billNumber, String productName, int quantity, double price, double total) {
        String date = LocalDate.now().format(DATE_FMT);
        // Format: billNumber|date|productName|quantity|price|total
        String line = billNumber + "|" + date + "|" + productName + "|" + quantity + "|" + price + "|" + total;
        fileManager.appendSaleRecord(line);
    }

    /** Logs a one-line summary of a completed bill to bill_history.txt. */
    public void recordBillSummary(String billNumber, double subtotal, double discount, double gst, double grandTotal, int itemCount) {
        String dateTime = LocalDateTime.now().format(DATETIME_FMT);
        // Format: billNumber|dateTime|subtotal|discount|gst|grandTotal|itemCount
        String line = billNumber + "|" + dateTime + "|" + subtotal + "|" + discount + "|" + gst + "|" + grandTotal + "|" + itemCount;
        fileManager.appendBillHistory(line);
    }

    /** Saves the full formatted receipt text into the Bills/ folder. */
    public void exportReceipt(String billNumber, String receiptText) {
        fileManager.exportBillToFile(billNumber, receiptText);
    }

    /** Small private aggregation holder - only ever used while building the report. */
    private static class ProductSales {
        int quantitySold = 0;
        double revenue = 0;
    }

    public void printSalesReport() {
        List<String> saleLines = fileManager.loadSaleRecords();
        List<String> billLines = fileManager.loadBillHistoryLines();

        if (saleLines.isEmpty() && billLines.isEmpty()) {
            System.out.println(Main.YELLOW + "No sales have been recorded yet." + Main.RESET);
            return;
        }

        String today = LocalDate.now().format(DATE_FMT);

        // ---- Per-product UNIT sales, from the sales.txt line items ----
        // (used for Highest/Lowest/Top-5; unaffected by any bill-level discount)
        Map<String, ProductSales> perProduct = new HashMap<>();
        for (String line : saleLines) {
            try {
                String[] parts = line.split("\\|", -1);
                if (parts.length != 6) continue;
                String productName = parts[2];
                int qty = Integer.parseInt(parts[3]);
                double total = Double.parseDouble(parts[5]);

                perProduct.putIfAbsent(productName, new ProductSales());
                ProductSales ps = perProduct.get(productName);
                ps.quantitySold += qty;
                ps.revenue += total;
            } catch (NumberFormatException e) {
                // skip malformed line - never let a bad record crash the report
            }
        }

        // ---- Bill-level REVENUE, from bill_history.txt grand totals ----
        // (the actual amount charged after discount + GST; used for every
        // money figure below so the headline numbers agree with the daily
        // summary instead of ignoring discounts)
        double todaysSales = 0;
        double totalRevenue = 0;
        int todaysBills = 0;
        Map<String, Double> perDayRevenue = new TreeMap<>();
        Map<String, Integer> perDayBillCount = new TreeMap<>();

        for (String line : billLines) {
            try {
                String[] parts = line.split("\\|", -1);
                if (parts.length != 7) continue;
                String dateTime = parts[1];
                String date = dateTime.split(" ")[0];
                double grandTotal = Double.parseDouble(parts[5]);

                totalRevenue += grandTotal;
                perDayRevenue.merge(date, grandTotal, Double::sum);
                perDayBillCount.merge(date, 1, Integer::sum);

                if (date.equals(today)) {
                    todaysSales += grandTotal;
                    todaysBills++;
                }
            } catch (NumberFormatException e) {
                // skip malformed line
            }
        }

        System.out.println(Main.CYAN + "\n========== SALES REPORT ==========" + Main.RESET);
        System.out.printf("Today's Sales            : %.2f%n", todaysSales);
        System.out.printf("Number of Bills (Today)  : %d%n", todaysBills);
        System.out.printf("Total Revenue (All-Time) : %.2f%n", totalRevenue);

        if (perProduct.isEmpty()) {
            System.out.println("Highest Selling Product  : N/A");
            System.out.println("Lowest Selling Product   : N/A");
        } else {
            String highest = null, lowest = null;
            int highestQty = Integer.MIN_VALUE, lowestQty = Integer.MAX_VALUE;
            for (Map.Entry<String, ProductSales> e : perProduct.entrySet()) {
                int q = e.getValue().quantitySold;
                if (q > highestQty) { highestQty = q; highest = e.getKey(); }
                if (q < lowestQty) { lowestQty = q; lowest = e.getKey(); }
            }
            System.out.println("Highest Selling Product  : " + highest + " (" + highestQty + " units)");
            System.out.println("Lowest Selling Product   : " + lowest + " (" + lowestQty + " units)");
        }

        // Bonus: Top 5 best-selling products
        System.out.println("\nTop 5 Best-Selling Products:");
        List<Map.Entry<String, ProductSales>> ranked = new ArrayList<>(perProduct.entrySet());
        ranked.sort((a, b) -> b.getValue().quantitySold - a.getValue().quantitySold);
        int limit = Math.min(5, ranked.size());
        if (limit == 0) {
            System.out.println("  No sales yet.");
        }
        for (int i = 0; i < limit; i++) {
            Map.Entry<String, ProductSales> e = ranked.get(i);
            System.out.printf("  %d. %-20s %d units | %.2f revenue%n",
                    i + 1, e.getKey(), e.getValue().quantitySold, e.getValue().revenue);
        }

        // Bonus: Daily sales summary
        System.out.println("\nDaily Sales Summary:");
        for (Map.Entry<String, Double> e : perDayRevenue.entrySet()) {
            int bills = perDayBillCount.getOrDefault(e.getKey(), 0);
            System.out.printf("  %s : %.2f revenue (%d bills)%n", e.getKey(), e.getValue(), bills);
        }
        System.out.println(Main.CYAN + "===================================" + Main.RESET);
    }
}
