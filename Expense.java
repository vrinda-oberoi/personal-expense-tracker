/**
 * Expense.java
 * Model class representing an individual expense record.
 * Demonstrates OOP Encapsulation with private fields, constructors,
 * getters, setters, and serialization methods.
 */
public class Expense {
    private int id;
    private String title;
    private double amount;
    private String category;
    private String date; // Format: YYYY-MM-DD
    private String description;

    /**
     * Default constructor
     */
    public Expense() {
    }

    /**
     * Parameterized constructor with ID (used when loading existing records)
     */
    public Expense(int id, String title, double amount, String category, String date, String description) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    /**
     * Parameterized constructor without ID (used when creating a new record before ID generation)
     */
    public Expense(String title, double amount, String category, String date, String description) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Converts this Expense object into a delimited string for file storage.
     * Uses '|' as the field separator, sanitizing pipes and newlines.
     */
    public String toDataString() {
        String cleanTitle = (title != null) ? title.replace("|", " ").trim() : "";
        String cleanCat = (category != null) ? category.replace("|", " ").trim() : "Other";
        String cleanDate = (date != null) ? date.replace("|", " ").trim() : "";
        String cleanDesc = (description != null) 
                ? description.replace("|", " ").replace("\n", " ").replace("\r", " ").trim() 
                : "";

        return id + "|" + cleanTitle + "|" + amount + "|" + cleanCat + "|" + cleanDate + "|" + cleanDesc;
    }

    /**
     * Creates an Expense object from a delimited file string.
     * Returns null if the line cannot be parsed.
     */
    public static Expense fromDataString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.split("\\|", -1);
        if (parts.length >= 6) {
            try {
                int id = Integer.parseInt(parts[0].trim());
                String title = parts[1].trim();
                double amount = Double.parseDouble(parts[2].trim());
                String category = parts[3].trim();
                String date = parts[4].trim();
                String description = parts[5].trim();

                return new Expense(id, title, amount, category, date, description);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing expense line: " + line);
                return null;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Expense [ID=" + id + ", Title=" + title + ", Amount=" + amount + 
               ", Category=" + category + ", Date=" + date + "]";
    }
}

