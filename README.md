# 🛒 Smart Grocery Store Billing & Inventory Management System

A command-line **Grocery Store Billing & Inventory Management System** built using **Core Java**. This project demonstrates object-oriented programming, file handling, collections, and exception handling without using any external libraries, databases, or GUI frameworks.

It allows administrators to manage inventory while customers can browse products, purchase items, generate bills, and maintain persistent sales records. All data is stored in local text files, ensuring that information is preserved even after the application is closed.

---

## 🚀 Features

* 🔐 Admin authentication
* 📦 Product Management (Add, Update, Delete)
* 📋 Inventory Management
* 🔍 Search products by ID, name, or category
* 📊 Sort products by name or price
* 🛒 Customer shopping cart
* 💰 Billing system with GST calculation
* 🎟️ Coupon support (`SAVE10`)
* 📄 Automatic bill generation
* 📈 Sales reports with revenue tracking
* ⚠️ Low-stock monitoring
* 💾 Persistent data storage using text files
* 🧾 Export every bill to a separate text file

---

## 🛠️ Tech Stack

* Java
* Object-Oriented Programming (OOP)
* Java Collections Framework
* File Handling
* Exception Handling
* LocalDateTime API

---

## 📸 Screenshots

### Home Menu
![Home Menu](c:\Users\HP\AppData\Local\Packages\MicrosoftWindows.Client.CBS_cw5n1h2txyewy\TempState\ScreenClip\{E951F876-3DDC-4B14-BAEA-6D5308570A13}.png)

### Admin Menu
![Admin Menu](c:\Users\HP\AppData\Local\Packages\MicrosoftWindows.Client.CBS_cw5n1h2txyewy\TempState\ScreenClip\{FC982929-7607-4E85-8EDC-26CD252D79FD}.png)

### Billing
![Billing](c:\Users\HP\AppData\Local\Packages\MicrosoftWindows.Client.CBS_cw5n1h2txyewy\TempState\ScreenClip\{8BB7DD95-C3C7-4BCE-A258-C23217479A86}.png)

### Sales Report
![Sales Report](c:\Users\HP\AppData\Local\Packages\MicrosoftWindows.Client.CBS_cw5n1h2txyewy\TempState\ScreenClip\{D6D7FD9A-F0C9-453D-BD91-AB1CC73ABF8B}.png)

## ▶️ How to Run

Make sure JDK 11 or above is installed (developed and tested on JDK 21).

```bash
javac *.java
java Main
```

On the first run, the application automatically creates:

* `products.txt`
* `sales.txt`
* `bill_history.txt`
* `Bills/` directory

All inventory updates, sales, and bills are saved immediately, so your data persists between sessions.

---

## 🔑 Default Admin Login

| Username | Password |
| -------- | -------- |
| admin    | admin123 |

---

## 📂 Project Structure

| File                    | Responsibility                                      |
| ----------------------- | --------------------------------------------------- |
| `Main.java`             | Application entry point and menu navigation         |
| `Admin.java`            | Admin authentication and management menu            |
| `Customer.java`         | Customer menu and shopping flow                     |
| `Product.java`          | Product model and serialization                     |
| `CartItem.java`         | Represents an item inside the shopping cart         |
| `InventoryManager.java` | Product CRUD, search, sorting, and stock management |
| `BillingSystem.java`    | Cart handling, billing, coupons, GST, and receipts  |
| `SalesManager.java`     | Bill numbering, sales history, and reports          |
| `FileManager.java`      | Centralized file input/output operations            |

---

## ✨ Highlights

* Search products by category
* Sort inventory by name or price
* Automatic bill numbering (`BILL0001`, `BILL0002`, ...)
* Low-stock detection
* Daily sales summary
* Top 5 best-selling products
* ANSI-colored terminal output
* Every completed bill is saved inside the `Bills/` folder
* Invalid user input is handled gracefully without crashing the application

---

## 📁 Data Storage

The application stores all information locally using text files.

```
products.txt
sales.txt
bill_history.txt
Bills/
```

The `|` character is used as the delimiter to safely support commas inside product names and supplier names.

---

## 🎯 Learning Outcomes

This project helped strengthen my understanding of:

* Object-Oriented Programming
* Java Collections Framework
* File Handling
* Exception Handling
* Data Persistence
* Modular Code Organization
* CLI Application Development

---

## 🚀 Future Improvements

* MySQL database integration
* Spring Boot REST API
* JavaFX desktop interface
* Role-based authentication
* Barcode support
* Product images
* Dashboard with analytics

---

## 👨‍💻 Author

**Suraj Singh**

If you found this project interesting, feel free to ⭐ the repository.
