import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * FileHandler.java
 * Handles permanent storage operations for expense records.
 * Uses Core Java File I/O (BufferedReader, BufferedWriter, FileReader, FileWriter).
 * Ensures safe handling of missing, empty, or corrupt files without crashing.
 */
public class FileHandler {
    private static final String FILE_NAME = "expenses.txt";

    /**
     * Reads all expense records from the local text file.
     * If the file does not exist, an empty list is returned safely.
     * 
     * @return ArrayList of Expense objects
     */
    public static ArrayList<Expense> loadExpenses() {
        ArrayList<Expense> expenses = new ArrayList<>();
        File file = new File(FILE_NAME);

        // If file does not exist yet, return an empty list safely
        if (!file.exists()) {
            System.out.println("No existing " + FILE_NAME + " found. Starting with empty database.");
            return expenses;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                // Skip empty lines or commented lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    Expense exp = Expense.fromDataString(line);
                    if (exp != null) {
                        expenses.add(exp);
                    } else {
                        System.err.println("Warning: Skipping invalid format at line " + lineNumber + ": " + line);
                    }
                } catch (Exception e) {
                    System.err.println("Warning: Error parsing line " + lineNumber + ": " + e.getMessage());
                }
            }
            System.out.println("Loaded " + expenses.size() + " expense record(s) from " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("Error reading " + FILE_NAME + ": " + e.getMessage());
        }

        return expenses;
    }

    /**
     * Saves the entire list of expense records to the local text file.
     * Overwrites previous content with updated state.
     * 
     * @param expenses ArrayList of Expense objects to persist
     * @return true if successfully saved, false otherwise
     */
    public static boolean saveExpenses(ArrayList<Expense> expenses) {
        if (expenses == null) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Expense exp : expenses) {
                writer.write(exp.toDataString());
                writer.newLine();
            }
            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to " + FILE_NAME + ": " + e.getMessage());
            return false;
        }
    }
}

