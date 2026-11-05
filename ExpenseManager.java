import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * ExpenseManager.java
 * Manages the in-memory collection of Expense objects using ArrayList.
 * Handles CRUD operations (Create, Read, Update, Delete) and dynamic calculations
 * such as category totals, monthly summaries, and recent transactions.
 * Coordinates with FileHandler for persistent data storage.
 */
public class ExpenseManager {
    private ArrayList<Expense> expenses;
    private int nextId;

    public ExpenseManager() {
        // Load stored records on initialization
        this.expenses = FileHandler.loadExpenses();
        
        // Determine the next available ID
        int maxId = 0;
        for (Expense exp : expenses) {
            if (exp.getId() > maxId) {
                maxId = exp.getId();
            }
        }
        this.nextId = maxId + 1;
    }

    /**
     * Adds a new expense record and persists the updated list to file.
     * 
     * @return The newly created Expense with assigned ID
     */
    public synchronized Expense addExpense(String title, double amount, String category, String date, String description) {
        Expense newExpense = new Expense(nextId++, title, amount, category, date, description);
        expenses.add(newExpense);
        FileHandler.saveExpenses(expenses);
        return newExpense;
    }

    /**
     * Updates an existing expense record by ID and saves changes to file.
     * 
     * @return true if updated, false if record not found
     */
    public synchronized boolean updateExpense(int id, String title, double amount, String category, String date, String description) {
        for (Expense exp : expenses) {
            if (exp.getId() == id) {
                exp.setTitle(title);
                exp.setAmount(amount);
                exp.setCategory(category);
                exp.setDate(date);
                exp.setDescription(description);
                FileHandler.saveExpenses(expenses);
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes an expense record by ID and saves changes to file.
     * 
     * @return true if deleted, false if record not found
     */
    public synchronized boolean deleteExpense(int id) {
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId() == id) {
                expenses.remove(i);
                FileHandler.saveExpenses(expenses);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a copy of all expense records.
     */
    public synchronized ArrayList<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    /**
     * Finds and returns an expense by ID.
     */
    public synchronized Expense getExpenseById(int id) {
        for (Expense exp : expenses) {
            if (exp.getId() == id) {
                return exp;
            }
        }
        return null;
    }

    /**
     * Calculates the sum total of all recorded expenses.
     */
    public synchronized double getTotalExpenses() {
        double total = 0.0;
        for (Expense exp : expenses) {
            total += exp.getAmount();
        }
        return total;
    }

    /**
     * Returns the total number of transactions recorded.
     */
    public synchronized int getTotalTransactions() {
        return expenses.size();
    }

    /**
     * Calculates the highest single expense recorded.
     */
    public synchronized Expense getHighestExpense() {
        if (expenses.isEmpty()) {
            return null;
        }
        Expense highest = expenses.get(0);
        for (Expense exp : expenses) {
            if (exp.getAmount() > highest.getAmount()) {
                highest = exp;
            }
        }
        return highest;
    }

    /**
     * Calculates the average amount spent per transaction.
     */
    public synchronized double getAverageExpense() {
        if (expenses.isEmpty()) {
            return 0.0;
        }
        return getTotalExpenses() / expenses.size();
    }

    /**
     * Calculates total spending grouped by Category.
     * Uses LinkedHashMap to preserve predictable display order.
     */
    public synchronized Map<String, Double> getCategoryWiseTotals() {
        Map<String, Double> categoryMap = new LinkedHashMap<>();
        String[] defaultCategories = {"Food", "Travel", "Shopping", "Bills", "Education", "Entertainment", "Other"};
        for (String cat : defaultCategories) {
            categoryMap.put(cat, 0.0);
        }

        for (Expense exp : expenses) {
            String cat = exp.getCategory();
            if (cat == null || cat.trim().isEmpty()) {
                cat = "Other";
            }
            categoryMap.put(cat, categoryMap.getOrDefault(cat, 0.0) + exp.getAmount());
        }
        return categoryMap;
    }

    /**
     * Calculates the number of transactions per Category.
     */
    public synchronized Map<String, Integer> getCategoryWiseCounts() {
        Map<String, Integer> countMap = new LinkedHashMap<>();
        String[] defaultCategories = {"Food", "Travel", "Shopping", "Bills", "Education", "Entertainment", "Other"};
        for (String cat : defaultCategories) {
            countMap.put(cat, 0);
        }

        for (Expense exp : expenses) {
            String cat = exp.getCategory();
            if (cat == null || cat.trim().isEmpty()) {
                cat = "Other";
            }
            countMap.put(cat, countMap.getOrDefault(cat, 0) + 1);
        }
        return countMap;
    }

    /**
     * Calculates total spending grouped by Month (Format: YYYY-MM).
     * Sorted in reverse chronological order (newest month first).
     */
    public synchronized Map<String, Double> getMonthlyTotals() {
        Map<String, Double> monthly = new TreeMap<>(Collections.reverseOrder());
        for (Expense exp : expenses) {
            String date = exp.getDate();
            String monthKey = "Unknown";
            if (date != null && date.length() >= 7) {
                monthKey = date.substring(0, 7);
            }
            monthly.put(monthKey, monthly.getOrDefault(monthKey, 0.0) + exp.getAmount());
        }
        return monthly;
    }

    /**
     * Calculates transaction count grouped by Month.
     */
    public synchronized Map<String, Integer> getMonthlyCounts() {
        Map<String, Integer> counts = new TreeMap<>(Collections.reverseOrder());
        for (Expense exp : expenses) {
            String date = exp.getDate();
            String monthKey = "Unknown";
            if (date != null && date.length() >= 7) {
                monthKey = date.substring(0, 7);
            }
            counts.put(monthKey, counts.getOrDefault(monthKey, 0) + 1);
        }
        return counts;
    }

    /**
     * Returns the latest 'count' expenses (e.g. for recent transactions table).
     */
    public synchronized ArrayList<Expense> getRecentExpenses(int count) {
        ArrayList<Expense> result = new ArrayList<>();
        int size = expenses.size();
        for (int i = size - 1; i >= 0 && result.size() < count; i--) {
            result.add(expenses.get(i));
        }
        return result;
    }

    /**
     * Filters expenses by keyword (matching title or description) and category.
     */
    public synchronized ArrayList<Expense> filterExpenses(String keyword, String category) {
        ArrayList<Expense> filtered = new ArrayList<>();
        String lowerKeyword = (keyword == null) ? "" : keyword.trim().toLowerCase();
        boolean filterCategory = (category != null && !category.equalsIgnoreCase("All Categories") && !category.trim().isEmpty());

        for (Expense exp : expenses) {
            boolean matchesCategory = !filterCategory || exp.getCategory().equalsIgnoreCase(category.trim());
            boolean matchesKeyword = lowerKeyword.isEmpty() ||
                    exp.getTitle().toLowerCase().contains(lowerKeyword) ||
                    exp.getDescription().toLowerCase().contains(lowerKeyword);

            if (matchesCategory && matchesKeyword) {
                filtered.add(exp);
            }
        }
        return filtered;
    }
}

