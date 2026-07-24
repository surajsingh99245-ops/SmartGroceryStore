# Smart Grocery Store Billing & Inventory Management System

A terminal-based grocery billing and inventory system written in **Core Java only**
(no database, no JavaFX/Swing, no external libraries) — Collections + File Handling
+ Exception Handling + OOP.

## How to run

Requires a JDK (11+ is fine; developed/tested on JDK 21).

```bash
javac *.java
java Main
```

That's it — no build tool needed. On first run it creates `products.txt`,
`sales.txt`, `bill_history.txt` and a `Bills/` folder in the working directory
automatically; every add/update/delete/restock/checkout saves immediately, so
you can close and reopen the app without losing data.

**Default admin login:** `admin` / `admin123`

## File structure

| File | Responsibility |
|---|---|
| `Main.java` | Entry point, top-level menu, shared safe-input + ANSI color helpers |
| `Product.java` | Product model (encapsulated fields, file (de)serialization) |
| `CartItem.java` | One line item in a customer's cart |
| `InventoryManager.java` | All product CRUD, search, sort, stock logic; owns `products.txt` |
| `BillingSystem.java` | Customer cart + checkout (coupon, GST, receipt generation) |
| `SalesManager.java` | Bill numbering, sale logging, the Sales Report |
| `FileManager.java` | All raw file I/O in one place (used by the two managers above) |
| `Admin.java` | Admin login + admin menu loop |
| `Customer.java` | Customer menu loop |

## Bonus features included

- Search by category (folded into the Search Product menu as a 3rd option)
- Sort inventory by name or price (prompted when you choose View Inventory)
- `SAVE10` coupon code at checkout for 10% off (invalid codes are ignored with a warning, not an error)
- Out-of-stock products are auto-flagged in both View Inventory and Low Stock Report
- Bill date/time via `LocalDateTime`, auto-incrementing bill numbers (`BILL0001`, `BILL0002`, ...)
- Top 5 best-selling products + a daily sales summary, both appended to the Sales Report
- Minimal ANSI color on headers/success/error text (most terminals render this; if yours shows raw
  `\u001B[...` codes, just ignore it — it doesn't affect functionality)
- Every completed bill is exported to `Bills/<BillNumber>.txt`

## A few judgment calls made where the spec was open to interpretation

- **Currency formatting:** all money values print with 2 decimal places (`220.50` rather than `220.5`)
  for consistency, even though the spec's own example shows plain integers like `210`.
- **Sales Report "Today's Sales" / "Number of Bills" / "Total Revenue":** the first two are scoped to
  *today*; Total Revenue is *all-time*. All three (and the Daily Sales Summary) are computed from the
  actual amount charged per bill (after any discount, plus GST) so the numbers always agree with each
  other.
- **Restock "Enter New Stock":** matches the spec's own worked example (`Current Stock: 5` +
  `Enter New Stock: 30` → `Updated Stock: 35`), so it's treated as the quantity to *add*, not the new total.
- **Buy Product stock check:** counts what's already sitting in the customer's cart before validating a
  new "Buy", so you can't add 5 of a product with only 5 in stock, then add 5 more before checking out.

## Notes

- Data files use `|` as a delimiter (not `,`) so product names/suppliers can safely contain commas.
- Every user-facing input path is wrapped so bad input (letters where a number is expected, an invalid
  menu choice, a missing product ID) prints a friendly message and returns to the menu — the app should
  never crash from user input.
