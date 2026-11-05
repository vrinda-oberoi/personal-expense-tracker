# Personal Expense Tracker – Java Semester Project

A modern, clean, and fully functional desktop application developed in **Core Java** using **Java Swing** and **AWT**. Designed specifically as a college semester project demonstrating core Object-Oriented Programming (OOP) concepts, file handling, exception handling, dynamic calculations, and event-driven GUI development without external dependencies or complex frameworks.

---

## 1. Project Introduction

Managing personal finances is a vital daily task. The **Personal Expense Tracker** offers an intuitive, desktop-based graphical interface that allows users to track their everyday expenses, organize spending by categories, analyze monthly expenditures, and maintain persistent records.

The application runs directly on any standard Java Development Kit (JDK 8 or higher) and stores all data locally in a text file (`expenses.txt`) using Core Java file I/O streams.

---

## 2. Project Objectives

- **Demonstrate Core Java Mastery:** Implement OOP principles, collections (`ArrayList`), file input/output streams, and robust exception handling.
- **Provide a User-Friendly GUI:** Design an aesthetic, responsive desktop UI with custom painted cards, alternating table row styling, and modern indigo accents.
- **Maintain Data Permanence:** Automatically persist all additions, edits, and deletions to a local text file without third-party databases.
- **Dynamic Analytics:** Calculate lifetime totals, category distributions, percentage breakdowns, and monthly summaries in real time.
- **Beginner & Viva Friendly:** Structured cleanly across dedicated classes with readable code and comprehensive comments for viva questions and project evaluation.

---

## 3. Key Application Features

### 📊 Dashboard
- **Total Expenses Metric Card:** Displays the cumulative expenditure formatted in local currency (`₹`).
- **Total Transactions Card:** Displays the total count of recorded transactions.
- **Top Category Card:** Shows the category with the highest spending and its amount.
- **Recent Transactions Table:** Lists the 5 most recent expense entries for quick review.
- **Quick Actions:** Direct navigation buttons to add expenses or view the complete table.

### ➕ Add Expense
- Form fields:
  - **Expense Title:** Short title describing the expense.
  - **Amount:** Numerical expenditure value in rupees (`₹`).
  - **Category:** Dropdown selection (`Food`, `Travel`, `Shopping`, `Bills`, `Education`, `Entertainment`, `Other`).
  - **Date:** Input field with validation (`YYYY-MM-DD`) and a one-click **"Today"** autofill button.
  - **Description:** Optional multi-line notes or remarks.
- **Validations:**
  - Non-empty title validation.
  - Strict numeric validation with positive value check (`amount > 0`).
  - Valid category selection check.
  - Strict calendar date format verification (`LocalDate` ISO parser).
- **Auto-Sync:** On submission, writes to `expenses.txt` and instantly updates all dashboard numbers and tables.

### 📋 View Expenses
- **Interactive JTable:** Displays all records with columns:
  - `ID`, `Title`, `Amount`, `Category`, `Date`, `Description`
- **Search & Filter:**
  - Filter transactions by keyword (title/description).
  - Filter by specific category.
- **Update Expense:**
  - Select an expense and click **"Update Expense"** to open a pre-filled edit dialog.
  - Validates and saves changes directly to disk and memory.
- **Delete Expense:**
  - Select an expense and click **"Delete Expense"**.
  - Displays a confirmation dialogue before safely removing the record from file storage.
- **Refresh:** Manual reload button to ensure synchronization.

### 📈 Expense Summary & Analytics
- **Summary KPI Cards:** Total Spent, Total Transactions, Average Spending per Transaction, and Largest Single Expense.
- **Category-Wise Spending Table:** Lists every category, total spent, percentage share (`%`), and transaction count.
- **Monthly Summary Table:** Groups expenses chronologically by month (`YYYY-MM`), showing monthly total expenditure, transaction count, and average expense.

---

## 4. Technologies Used

| Technology | Purpose |
|---|---|
| **Java (JDK 8 / 11 / 17 / 21 / 23)** | Core programming language |
| **Java Swing (`javax.swing.*`)** | Desktop graphical user interface components |
| **Java AWT (`java.awt.*`)** | Layout managers, colors, fonts, event handling, and 2D painting |
| **ArrayList (`java.util.ArrayList`)** | Dynamic in-memory expense collection management |
| **File I/O (`BufferedReader`, `BufferedWriter`)** | Persistent storage in `expenses.txt` |
| **Exception Handling (`try-catch`)** | Graceful error handling for input validation and file I/O |

> **Zero External Dependencies:** Built entirely with standard Java SE libraries. No Maven, Gradle, Spring Boot, MySQL, or Node.js required.

---

## 5. Core Java Concepts Implemented

1. **Object-Oriented Programming (OOP):**
   - **Encapsulation:** `Expense` class utilizes private fields with public getters, setters, and constructors.
   - **Separation of Concerns:** Business logic (`ExpenseManager`), File I/O (`FileHandler`), Data Model (`Expense`), and UI (`Dashboard`, panels) are separated into individual classes.
2. **Collections Framework:**
   - Uses `ArrayList<Expense>` for dynamic list manipulation (add, remove, search, filter).
   - Uses `LinkedHashMap` and `TreeMap` for category aggregations and sorted monthly analytics.
3. **File Handling:**
   - `FileReader` & `BufferedReader` for fast line-by-line file reading.
   - `FileWriter` & `BufferedWriter` with try-with-resources for automatic resource closing and safe file persistence.
   - Custom pipe-delimited format (`|`) with delimiter sanitization.
4. **Exception Handling:**
   - `NumberFormatException` handling for numeric fields.
   - `DateTimeParseException` handling for date inputs.
   - `IOException` handling for file access.
5. **Event-Driven Programming:**
   - `ActionListener` and `MouseListener` for button clicks, selection changes, and hover transitions.
   - `SwingUtilities.invokeLater` to ensure GUI thread safety on the Event Dispatch Thread (EDT).

---

## 6. Project Folder Structure

```text
expense-tracker-java/
├── Expense.java              # Model class (encapsulation, getters/setters, serialization)
├── FileHandler.java          # File I/O operations (BufferedReader, BufferedWriter)
├── ExpenseManager.java       # Business logic (CRUD, calculations, aggregations)
├── UITheme.java              # Design constants (colors, fonts, button styling, table renderer)
├── RoundedPanel.java         # Custom JPanel with anti-aliased rounded borders
├── Dashboard.java            # Main application window, sidebar navigation, CardLayout
├── AddExpensePanel.java      # Add expense form with validation
├── ViewExpensesPanel.java    # JTable view with search, edit dialog, and deletion
├── SummaryPanel.java         # Dynamic category & monthly analytics
├── Main.java                 # Application entry point (main method)
├── TestScenarios.java        # Automated test suite validating all 9 functional scenarios
├── expenses.txt              # Persistent data file (auto-created on run)
├── compile.bat               # Windows batch script to compile source code
├── run.bat                   # Windows batch script to compile and launch app
├── README.md                 # Complete project documentation
└── PROJECT_REPORT.md         # College semester project report outline
```

---

## 7. How to Compile and Run

### Option A: Using Windows Batch Script (Easiest)
Simply double-click **`run.bat`** in the project folder. It will compile all source files and launch the application automatically.

---

### Option B: Using Command Line (Terminal / CMD / PowerShell)
Make sure JDK is installed and configured in your system `PATH`.

1. Open terminal inside the project directory:
   ```bash
   cd "path/to/expense tracker java"
   ```

2. Compile all Java files:
   ```bash
   javac *.java
   ```

3. Run the application:
   ```bash
   java Main
   ```

---

### Option C: Using Visual Studio Code
1. Open Visual Studio Code.
2. Choose **File > Open Folder...** and select the `expense tracker java` folder.
3. Install the **Extension Pack for Java** (by Microsoft) if not already installed.
4. Open `Main.java` and click the **Run** button (or press `F5` / `Ctrl+F5`).

---

### Option D: Using IntelliJ IDEA
1. Open IntelliJ IDEA.
2. Select **Open** and navigate to the `expense tracker java` folder.
3. In Project Structure (`File > Project Structure`), ensure a JDK (version 8 or newer) is selected.
4. Open `Main.java`, right-click inside the editor, and select **Run 'Main.main()'**.

---

## 8. Automated Test Suite (Verification)

To demonstrate functional correctness to your professor during viva, an automated test script is included:

```bash
javac TestScenarios.java
java TestScenarios
```

This validates all 9 required operational scenarios:
1. Adding multiple expenses.
2. File saving and application restart simulation.
3. Verifying persisted records from `expenses.txt`.
4. Updating an expense and verifying disk modifications.
5. Deleting an expense and verifying removal from file.
6. Re-calculating total expenses dynamically.
7. Category-wise and monthly breakdown verification.
8. Testing invalid inputs, edge cases, and delimiter collisions.
9. Missing data file handling without crashes.

---

## 9. Sample Screenshots Section

### 1. Main Dashboard Overview
*Features metric cards for Total Expenses, Total Transactions, Top Category, and the Recent 5 Transactions table with quick action buttons.*

### 2. Add Expense Form
*Clean, card-based form with fields for Title, Amount, Category dropdown, Date (with 'Today' autofill), and Description with strict validation alerts.*

### 3. View & Manage Expenses
*Interactive JTable showing all records with keyword search, category filter, Update Expense dialog, and Delete Expense confirmation prompt.*

### 4. Expense Summary & Analytics
*Detailed breakdown displaying Category-Wise spending with percentage share and Monthly chronological expense breakdown.*

---

## 10. Future Scope

For future expansion beyond the semester curriculum, the project could incorporate:
- Exporting records to CSV or PDF reports.
- Graphical charts (pie charts and bar graphs using Java 2D graphics).
- Budget limits with warning notifications when spending exceeds a category threshold.
- User authentication and multi-user profile support.
- Recurring expense scheduling.

