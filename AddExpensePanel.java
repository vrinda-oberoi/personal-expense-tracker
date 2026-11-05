import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * AddExpensePanel.java
 * Provides a user-friendly form to add new expenses.
 * Implements strict input validation for Title, Amount, Category, and Date.
 * Updates data storage and triggers a dashboard refresh upon successful addition.
 */
public class AddExpensePanel extends JPanel {
    private ExpenseManager expenseManager;
    private Dashboard dashboard;

    private JTextField titleField;
    private JTextField amountField;
    private JComboBox<String> categoryBox;
    private JTextField dateField;
    private JTextArea descriptionArea;

    public AddExpensePanel(ExpenseManager expenseManager, Dashboard dashboard) {
        this.expenseManager = expenseManager;
        this.dashboard = dashboard;

        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        initComponents();
    }

    private void initComponents() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Add New Expense");
        titleLabel.setFont(UITheme.FONT_TITLE_LARGE);
        titleLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel subtitleLabel = new JLabel("Record a new personal transaction with category and details");
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // --- Center Form Card ---
        RoundedPanel formCard = new RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // 1. Expense Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formCard.add(createFieldLabel("Expense Title *", "e.g., Grocery Shopping, Monthly Rent, Bus Pass"), gbc);

        gbc.gridy = 1;
        titleField = createStyledTextField();
        formCard.add(titleField, gbc);

        // 2. Amount and Category side by side
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.5;
        formCard.add(createFieldLabel("Amount (₹) *", "Numeric value greater than 0"), gbc);

        gbc.gridx = 1;
        formCard.add(createFieldLabel("Category *", "Select transaction category"), gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        amountField = createStyledTextField();
        formCard.add(amountField, gbc);

        gbc.gridx = 1;
        String[] categories = {"Food", "Travel", "Shopping", "Bills", "Education", "Entertainment", "Other"};
        categoryBox = new JComboBox<>(categories);
        categoryBox.setFont(UITheme.FONT_REGULAR);
        categoryBox.setBackground(Color.WHITE);
        categoryBox.setPreferredSize(new Dimension(200, 36));
        formCard.add(categoryBox, gbc);

        // 3. Date with "Today" button
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        formCard.add(createFieldLabel("Date *", "Format: YYYY-MM-DD (e.g. 2026-11-05)"), gbc);

        gbc.gridy = 5;
        JPanel dateRowPanel = new JPanel(new BorderLayout(8, 0));
        dateRowPanel.setOpaque(false);
        dateField = createStyledTextField();
        dateField.setText(LocalDate.now().toString());

        JButton todayBtn = UITheme.createSecondaryButton("Today");
        todayBtn.setPreferredSize(new Dimension(80, 36));
        todayBtn.addActionListener(e -> dateField.setText(LocalDate.now().toString()));

        dateRowPanel.add(dateField, BorderLayout.CENTER);
        dateRowPanel.add(todayBtn, BorderLayout.EAST);
        formCard.add(dateRowPanel, gbc);

        // 4. Description
        gbc.gridy = 6;
        formCard.add(createFieldLabel("Description / Notes (Optional)", "Add any extra details or remarks"), gbc);

        gbc.gridy = 7;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(UITheme.FONT_REGULAR);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        formCard.add(descScroll, gbc);

        // 5. Action Buttons
        gbc.gridy = 8;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setOpaque(false);

        JButton clearBtn = UITheme.createSecondaryButton("Reset Form");
        clearBtn.addActionListener(e -> clearForm());

        JButton submitBtn = UITheme.createPrimaryButton("Save Expense");
        submitBtn.addActionListener(e -> handleAddExpense());

        btnPanel.add(clearBtn);
        btnPanel.add(submitBtn);
        formCard.add(btnPanel, gbc);

        // Wrap form card inside a scroll pane so it handles any window size gracefully
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        centerWrapper.setOpaque(false);
        formCard.setPreferredSize(new Dimension(680, 480));
        centerWrapper.add(formCard);

        JScrollPane scrollPane = new JScrollPane(centerWrapper);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createFieldLabel(String labelText, String subtext) {
        JLabel label = new JLabel("<html><b>" + labelText + "</b> <span style='color:#64748B;font-size:10px;'>- " + subtext + "</span></html>");
        label.setFont(UITheme.FONT_BOLD);
        label.setForeground(UITheme.TEXT_MAIN);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(UITheme.FONT_REGULAR);
        tf.setPreferredSize(new Dimension(200, 36));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    private void handleAddExpense() {
        String title = titleField.getText().trim();
        String amountStr = amountField.getText().trim();
        String category = (String) categoryBox.getSelectedItem();
        String dateStr = dateField.getText().trim();
        String description = descriptionArea.getText().trim();

        // 1. Title Validation
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Expense Title cannot be empty. Please enter a valid title.", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
            titleField.requestFocus();
            return;
        }

        // 2. Amount Validation
        if (amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Amount cannot be empty. Please enter a numeric amount.", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
            amountField.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, 
                        "Expense Amount must be strictly greater than zero.", 
                        "Validation Error", 
                        JOptionPane.WARNING_MESSAGE);
                amountField.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Invalid Amount! Please enter a valid numeric number (e.g., 250 or 49.99).", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
            amountField.requestFocus();
            return;
        }

        // 3. Category Validation
        if (category == null || category.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Please select an expense category.", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
            categoryBox.requestFocus();
            return;
        }

        // 4. Date Validation
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Date cannot be empty. Please provide date in YYYY-MM-DD format.", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
            dateField.requestFocus();
            return;
        }

        try {
            LocalDate.parse(dateStr);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Invalid Date format! Please use YYYY-MM-DD (e.g., 2026-11-05).", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
            dateField.requestFocus();
            return;
        }

        // Add expense via manager
        Expense added = expenseManager.addExpense(title, amount, category, dateStr, description);

        // Feedback and refresh
        JOptionPane.showMessageDialog(this, 
                "Expense added successfully!\nID: #" + added.getId() + " - " + added.getTitle() + 
                " (" + UITheme.formatCurrency(added.getAmount()) + ")", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);

        clearForm();

        // Refresh dashboard overview and table data across all panels
        if (dashboard != null) {
            dashboard.refreshAllPanels();
        }
    }

    public void clearForm() {
        titleField.setText("");
        amountField.setText("");
        if (categoryBox.getItemCount() > 0) {
            categoryBox.setSelectedIndex(0);
        }
        dateField.setText(LocalDate.now().toString());
        descriptionArea.setText("");
        titleField.requestFocus();
    }
}

