# College Semester Project Report Outline

## Personal Expense Tracker Desktop Application
**Course:** Object Oriented Programming with Java / Core Java Laboratory  
**Technology:** Core Java (JDK 8+), Java Swing, Java AWT, File Handling  
**Year / Semester:** Academic Year 2026-2027  

---

## Table of Contents
1. **Introduction**
   - 1.1 Project Overview
   - 1.2 Problem Statement
   - 1.3 Proposed System & Objectives
2. **System Requirements Specification (SRS)**
   - 2.1 Hardware Requirements
   - 2.2 Software Requirements
   - 2.3 User Characteristics
3. **System Architecture & Design**
   - 3.1 Object-Oriented System Architecture
   - 3.2 Class Diagram & Component Descriptions
   - 3.3 Data Flow & Storage Design (`expenses.txt`)
4. **Implementation Details**
   - 4.1 Data Model Layer (`Expense.java`)
   - 4.2 File Persistence Layer (`FileHandler.java`)
   - 4.3 Business Logic Layer (`ExpenseManager.java`)
   - 4.4 Presentation Layer (Swing GUI Components)
   - 4.5 Validation & Exception Handling Mechanism
5. **Testing & Quality Assurance**
   - 5.1 Test Plan & Methodology
   - 5.2 Test Cases & Execution Results
   - 5.3 Edge Cases & Fault Tolerance
6. **Results & Discussion**
   - 6.1 Functional Capabilities
   - 6.2 Key Advantages of the System
7. **Conclusion & Future Enhancements**
8. **Appendix: Frequently Asked Viva Questions & Answers**

---

## 1. Introduction

### 1.1 Project Overview
The **Personal Expense Tracker** is a desktop application developed using Core Java and the Java Swing GUI framework. The system is designed to provide individuals with an easy-to-use tool to record, monitor, update, and analyze their daily financial expenses. The application eliminates dependencies on heavy web frameworks or database servers by persisting data in a local delimited text file using Java's standard File I/O streams.

### 1.2 Problem Statement
Traditional expense logging methods such as physical notebooks or manual spreadsheets are prone to omissions, math errors, loss of physical records, and lack of instant categorization. On the other hand, commercial enterprise software is often overly complex, requires recurring subscriptions, and demands internet connectivity. A lightweight, offline, cross-platform desktop application provides an ideal balance of privacy, speed, and simplicity.

### 1.3 Proposed System & Objectives
- To demonstrate practical mastery of Core Java concepts (OOP, Encapsulation, Collections, File Streams, Exception Handling).
- To create a responsive desktop GUI with interactive tables and real-time dashboard updates.
- To implement dynamic spending analytics across categories and monthly timeframes.
- To ensure zero external dependency deployment that compiles and runs on any standard JDK.

---

## 2. System Requirements Specification (SRS)

### 2.1 Hardware Requirements
- **Processor:** Intel Core i3 / AMD Ryzen 3 or equivalent.
- **RAM:** Minimum 2 GB (4 GB recommended).
- **Hard Disk Space:** 50 MB available storage for JDK and project files.
- **Display:** 1024 x 768 minimum resolution.

### 2.2 Software Requirements
- **Operating System:** Windows 10/11, macOS, or Linux.
- **Platform / Runtime:** Java SE Development Kit (JDK 8, 11, 17, 21, or 23).
- **Text Editor / IDE:** Any Java IDE (VS Code, IntelliJ IDEA, Eclipse, NetBeans) or plain Command Prompt.

---

## 3. System Architecture & Design

### 3.1 Object-Oriented Architecture
The application adheres to a modular multi-tier architecture:

```text
+-----------------------------------------------------------+
|                     PRESENTATION LAYER                    |
|  Main.java | Dashboard.java | AddExpensePanel.java        |
|  ViewExpensesPanel.java     | SummaryPanel.java           |
+-----------------------------------------------------------+
                              |
                              v
+-----------------------------------------------------------+
|                    BUSINESS LOGIC LAYER                   |
|                   ExpenseManager.java                     |
|    - CRUD operations       - Calculations & Aggregations  |
|    - Category breakdowns   - Monthly summaries            |
+-----------------------------------------------------------+
               |                             |
               v                             v
+-----------------------------+ +---------------------------+
|         MODEL LAYER         | |     PERSISTENCE LAYER     |
|         Expense.java        | |      FileHandler.java     |
| (Private fields, accessors, | | (BufferedReader/Writer,   |
|  constructors, formatters)  | |  expenses.txt storage)    |
+-----------------------------+ +---------------------------+
```

### 3.2 Class Responsibilities

1. **`Main.java`**: Configures native Look & Feel and launches the GUI on the Event Dispatch Thread (EDT).
2. **`Expense.java`**: Model class encapsulating attributes: `id`, `title`, `amount`, `category`, `date`, `description`.
3. **`FileHandler.java`**: Manages low-level file I/O operations using `BufferedReader`, `BufferedWriter`, `FileReader`, and `FileWriter`.
4. **`ExpenseManager.java`**: Central repository maintaining an `ArrayList<Expense>`. Executes addition, modification, deletion, search filters, and statistical calculations.
5. **`Dashboard.java`**: Main `JFrame` housing sidebar navigation and a `CardLayout` container switching between sub-panels.
6. **`AddExpensePanel.java`**: Form panel with input verification for title, numeric amount, category selection, and ISO calendar date.
7. **`ViewExpensesPanel.java`**: Displays all records in a styled `JTable`, facilitating searches, updates via dialogs, and deletion with confirmation.
8. **`SummaryPanel.java`**: Calculates KPI metrics, category percentage shares, and chronological monthly tables.
9. **`UITheme.java` & `RoundedPanel.java`**: Reusable styling utility providing colors, fonts, table decorators, and custom-painted rounded cards.

### 3.3 Storage Format Design (`expenses.txt`)
Data is saved in a pipe-delimited format:
```text
ID|Title|Amount|Category|Date|Description
```
*Example record:*
```text
1|Grocery & Supermarket|1200.0|Food|2026-11-01|Monthly kitchen supplies
```
- Delimiters within user inputs are sanitized upon serialization (`title.replace("|", " ")`) to prevent delimiter collision.
- Missing files and empty lines are handled safely without application crash.

---

## 4. Key Implementation Highlights

### 4.1 Encapsulation and Data Integrity
All attributes in `Expense.java` are marked `private` and only accessible via validated public getters and setters.

### 4.2 File Handling with Try-with-Resources
```java
try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
    for (Expense exp : expenses) {
        writer.write(exp.toDataString());
        writer.newLine();
    }
} catch (IOException e) {
    System.err.println("Error saving expenses: " + e.getMessage());
}
```
Automatic resource management guarantees file streams are safely flushed and closed even if runtime exceptions occur.

### 4.3 Input Validation Flow
```java
// Numerical validation
double amount = Double.parseDouble(amountStr);
if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");

// Date format validation
LocalDate.parse(dateStr); // Throws DateTimeParseException if invalid
```

---

## 5. Testing & Verification

| Test Case ID | Test Scenario | Input Data | Expected Output | Status |
|---|---|---|---|---|
| **TC-01** | Missing Data File | `expenses.txt` deleted | App loads with 0 records; creates file on save | **PASS** |
| **TC-02** | Add Valid Expense | "Coffee", 150.00, "Food", "2026-11-05" | Record saved; ID generated; Dashboard refreshed | **PASS** |
| **TC-03** | Empty Title Validation | Title = `""` | Warning dialog: "Title cannot be empty" | **PASS** |
| **TC-04** | Negative Amount Validation | Amount = `-50` | Warning dialog: "Amount must be greater than zero" | **PASS** |
| **TC-05** | Non-numeric Amount | Amount = `"abc"` | Error dialog: "Invalid Amount entered" | **PASS** |
| **TC-06** | Invalid Date Format | Date = `"32-13-2026"` | Error dialog: "Invalid Date format" | **PASS** |
| **TC-07** | Persistence Verification | Restart application | All 5 test records loaded accurately from disk | **PASS** |
| **TC-08** | Update Existing Record | Edit amount from 200 to 250 | In-memory and file record updated; total adjusted | **PASS** |
| **TC-09** | Delete Record | Select record #5 and delete | Confirmation dialog appears; record removed from file | **PASS** |
| **TC-10** | Dynamic Category Analytics | Multiple categories | Category totals and percentage shares match 100% | **PASS** |

---

## 6. Appendix: Frequently Asked Viva Questions & Answers

### Q1: Why did you use `ArrayList` instead of a standard Java array?
**Answer:** A standard array in Java has a fixed size defined at creation time. In an expense tracking application, the number of transactions changes dynamically as users add and delete records. `ArrayList` dynamically resizes itself, provides built-in methods (`add`, `remove`, `get`, `size`), and is part of the Java Collections Framework.

### Q2: What is the purpose of `BufferedReader` and `BufferedWriter`?
**Answer:** `FileReader` and `FileWriter` read/write data byte-by-byte or character-by-character from the hard drive, which is slow. Wrapping them with `BufferedReader` and `BufferedWriter` creates an in-memory buffer that reads and writes chunks of data in blocks, dramatically improving I/O performance and providing helpful methods like `readLine()` and `newLine()`.

### Q3: Why is `SwingUtilities.invokeLater()` used in `Main.java`?
**Answer:** Swing GUI components are not thread-safe. All modifications and rendering of GUI components must occur on a single dedicated background thread known as the **Event Dispatch Thread (EDT)**. `SwingUtilities.invokeLater()` schedules the GUI initialization task on the EDT to prevent UI freezing and race conditions.

### Q4: How is data consistency maintained between the UI and the file?
**Answer:** Any mutating action (adding, updating, or deleting an expense) updates the in-memory `ArrayList` in `ExpenseManager` and immediately invokes `FileHandler.saveExpenses(expenses)` to synchronize changes with `expenses.txt`. Immediately following the save, `dashboard.refreshAllPanels()` is called to synchronize the tables and metric cards.

### Q5: How do you handle file delimiters if the user enters the delimiter symbol in the title?
**Answer:** In `Expense.toDataString()`, any occurrence of the `|` delimiter character inside user-provided titles or descriptions is sanitized using `.replace("|", " ")` before saving to disk. This prevents corrupting the delimiter count during deserialization in `Expense.fromDataString()`.

