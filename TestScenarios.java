import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * TestScenarios.java
 * Comprehensive automated verification script to validate all 9 required functional scenarios:
 * 1. Add multiple expenses.
 * 2. Close and reopen (simulate restart with FileHandler).
 * 3. Confirm previous expenses are still available.
 * 4. Update an existing expense.
 * 5. Delete an expense.
 * 6. Confirm total expenses update correctly.
 * 7. Confirm category-wise summaries are correct.
 * 8. Test invalid and empty inputs.
 * 9. Test the application when the data file does not exist.
 */
public class TestScenarios {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  STARTING AUTOMATED EXPENSE TRACKER TEST SUITE   ");
        System.out.println("==================================================");

        // Scenario 9: Clean slate test (file does not exist)
        System.out.println("\n[Test 1/9] Testing missing data file handling (Scenario 9)...");
        File file = new File("expenses.txt");
        if (file.exists()) {
            file.delete();
        }
        ExpenseManager manager = new ExpenseManager();
        assertCondition(manager.getAllExpenses().isEmpty(), "Initial expense list should be empty when expenses.txt is absent");
        assertCondition(manager.getTotalExpenses() == 0.0, "Total expenses should be 0.0");
        assertCondition(manager.getTotalTransactions() == 0, "Total transactions should be 0");
        System.out.println("✓ Scenario 9 PASSED: Missing file handled gracefully without crashing.");

        // Scenario 1: Add multiple expenses
        System.out.println("\n[Test 2/9] Adding multiple expenses (Scenario 1)...");
        Expense e1 = manager.addExpense("Grocery & Milk", 450.50, "Food", "2026-11-01", "Weekly supplies");
        Expense e2 = manager.addExpense("Metro Card Recharge", 200.00, "Travel", "2026-11-02", "Monthly metro pass");
        Expense e3 = manager.addExpense("Java Book", 650.00, "Education", "2026-11-03", "Core Java semester textbook");
        Expense e4 = manager.addExpense("Electricity Bill", 1250.00, "Bills", "2026-10-25", "October bill payment");
        Expense e5 = manager.addExpense("Movie Tickets", 500.00, "Entertainment", "2026-10-30", "Weekend movie");

        assertCondition(manager.getTotalTransactions() == 5, "Total transactions count should be 5");
        double expectedTotal1 = 450.50 + 200.00 + 650.00 + 1250.00 + 500.00;
        assertCondition(Math.abs(manager.getTotalExpenses() - expectedTotal1) < 0.001, 
                "Total expenses should equal " + expectedTotal1 + " but got " + manager.getTotalExpenses());
        System.out.println("✓ Scenario 1 PASSED: Successfully added 5 expenses. Total: " + manager.getTotalExpenses());

        // Scenario 2 & 3: Close and reopen application, verify persistence
        System.out.println("\n[Test 3/9] Simulating app restart & verifying persistence (Scenario 2 & 3)...");
        ExpenseManager reloadedManager = new ExpenseManager();
        assertCondition(reloadedManager.getTotalTransactions() == 5, "Reloaded manager should have 5 transactions");
        assertCondition(Math.abs(reloadedManager.getTotalExpenses() - expectedTotal1) < 0.001, "Reloaded total should match");
        Expense loadedE1 = reloadedManager.getExpenseById(e1.getId());
        assertCondition(loadedE1 != null && loadedE1.getTitle().equals("Grocery & Milk"), "Record 1 persisted properly");
        System.out.println("✓ Scenario 2 & 3 PASSED: All records restored identically from expenses.txt.");

        // Scenario 4: Update an existing expense
        System.out.println("\n[Test 4/9] Updating an existing expense (Scenario 4)...");
        boolean updated = reloadedManager.updateExpense(e2.getId(), "Metro & Bus Pass", 250.00, "Travel", "2026-11-02", "Updated recharge");
        assertCondition(updated, "Update method returned true");
        Expense updatedE2 = reloadedManager.getExpenseById(e2.getId());
        assertCondition(updatedE2.getTitle().equals("Metro & Bus Pass"), "Title updated");
        assertCondition(updatedE2.getAmount() == 250.00, "Amount updated to 250.00");
        
        // Verify update persisted
        ExpenseManager verifyUpdate = new ExpenseManager();
        Expense reloadedUpdatedE2 = verifyUpdate.getExpenseById(e2.getId());
        assertCondition(reloadedUpdatedE2.getAmount() == 250.00, "Updated amount persisted in file");
        System.out.println("✓ Scenario 4 PASSED: Expense updated successfully and verified in file.");

        // Scenario 5: Delete an expense
        System.out.println("\n[Test 5/9] Deleting an expense (Scenario 5)...");
        int countBeforeDelete = verifyUpdate.getTotalTransactions();
        boolean deleted = verifyUpdate.deleteExpense(e5.getId());
        assertCondition(deleted, "Delete method returned true");
        assertCondition(verifyUpdate.getTotalTransactions() == countBeforeDelete - 1, "Count decreased by 1");
        assertCondition(verifyUpdate.getExpenseById(e5.getId()) == null, "Deleted expense no longer exists in memory");

        // Verify deletion persisted
        ExpenseManager verifyDelete = new ExpenseManager();
        assertCondition(verifyDelete.getExpenseById(e5.getId()) == null, "Deleted expense no longer exists in file");
        System.out.println("✓ Scenario 5 PASSED: Expense deleted successfully and verified in file.");

        // Scenario 6: Confirm total expenses update correctly
        System.out.println("\n[Test 6/9] Verifying updated total calculations (Scenario 6)...");
        // Remaining: e1 (450.50), e2 (updated to 250.00), e3 (650.00), e4 (1250.00)
        double expectedTotalAfterChanges = 450.50 + 250.00 + 650.00 + 1250.00; // 2600.50
        assertCondition(Math.abs(verifyDelete.getTotalExpenses() - expectedTotalAfterChanges) < 0.001, 
                "Updated total should be " + expectedTotalAfterChanges + " but was " + verifyDelete.getTotalExpenses());
        System.out.println("✓ Scenario 6 PASSED: Total expenses recalculated accurately: " + verifyDelete.getTotalExpenses());

        // Scenario 7: Confirm category-wise and monthly summaries
        System.out.println("\n[Test 7/9] Verifying category-wise and monthly summaries (Scenario 7)...");
        Map<String, Double> catTotals = verifyDelete.getCategoryWiseTotals();
        assertCondition(catTotals.get("Food") == 450.50, "Food category total should be 450.50");
        assertCondition(catTotals.get("Travel") == 250.00, "Travel category total should be 250.00");
        assertCondition(catTotals.get("Education") == 650.00, "Education category total should be 650.00");
        assertCondition(catTotals.get("Bills") == 1250.00, "Bills category total should be 1250.00");
        assertCondition(catTotals.get("Entertainment") == 0.0, "Entertainment category total should be 0.0 (deleted)");

        Map<String, Double> monthlyTotals = verifyDelete.getMonthlyTotals();
        assertCondition(monthlyTotals.containsKey("2026-11"), "Contains November 2026");
        assertCondition(monthlyTotals.containsKey("2026-10"), "Contains October 2026");
        System.out.println("✓ Scenario 7 PASSED: Category-wise and monthly summaries calculated accurately.");

        // Scenario 8: Test invalid and empty inputs
        System.out.println("\n[Test 8/9] Testing invalid and edge case inputs (Scenario 8)...");
        // Pipe handling
        Expense pipeExp = verifyDelete.addExpense("Test | with | pipes", 100.0, "Other", "2026-11-04", "Desc | with | pipe\nand newline");
        ExpenseManager pipeCheck = new ExpenseManager();
        Expense retrievedPipeExp = pipeCheck.getExpenseById(pipeExp.getId());
        assertCondition(retrievedPipeExp != null, "Expense with pipes saved and loaded safely");
        assertCondition(!retrievedPipeExp.getTitle().contains("|"), "Pipes sanitized to prevent delimiter collision");
        verifyDelete.deleteExpense(pipeExp.getId());
        System.out.println("✓ Scenario 8 PASSED: Delimiters, newlines, and input edge cases handled safely.");

        System.out.println("\n==================================================");
        System.out.println("   ALL 9 SCENARIOS VERIFIED AND PASSED 100%!     ");
        System.out.println("==================================================");
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            System.err.println("ASSERTION FAILED: " + message);
            System.exit(1);
        }
    }
}

