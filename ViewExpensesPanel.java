import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * ViewExpensesPanel.java
 * Displays all recorded expenses in a formatted, interactive JTable.
 * Supports updating selected expenses, deleting records with confirmation,
 * refreshing table data, and filtering by keyword or category.
 */
public class ViewExpensesPanel extends JPanel {
    private ExpenseManager expenseManager;
    private Dashboard dashboard;

    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterCategoryBox;
    private JLabel countLabel;

    public ViewExpensesPanel(ExpenseManager expenseManager, Dashboard dashboard) {
        this.expenseManager = expenseManager;
        this.dashboard = dashboard;

        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        initComponents();
        loadTableData();
    }

    private void initComponents() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("All Recorded Expenses");
        titleLabel.setFont(UITheme.FONT_TITLE_LARGE);
        titleLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel subtitleLabel = new JLabel("Browse, search, edit, or delete stored expense transactions");
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // --- Center Container (Filter Bar + Table) ---
        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setOpaque(false);

        // Filter / Search Toolbar
        RoundedPanel filterBar = new RoundedPanel(10, UITheme.CARD_BG, UITheme.BORDER);
        filterBar.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));

        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(UITheme.FONT_BOLD);
        searchLbl.setForeground(UITheme.TEXT_MAIN);

        searchField = new JTextField(15);
        searchField.setFont(UITheme.FONT_REGULAR);
        searchField.setPreferredSize(new Dimension(180, 32));

        JLabel catLbl = new JLabel("Category:");
        catLbl.setFont(UITheme.FONT_BOLD);
        catLbl.setForeground(UITheme.TEXT_MAIN);

        String[] filterOptions = {"All Categories", "Food", "Travel", "Shopping", "Bills", "Education", "Entertainment", "Other"};
        filterCategoryBox = new JComboBox<>(filterOptions);
        filterCategoryBox.setFont(UITheme.FONT_REGULAR);
        filterCategoryBox.setPreferredSize(new Dimension(140, 32));

        JButton searchBtn = UITheme.createPrimaryButton("Filter");
        searchBtn.setPreferredSize(new Dimension(80, 32));
        searchBtn.addActionListener(e -> applyFilter());

        JButton resetFilterBtn = UITheme.createSecondaryButton("Reset");
        resetFilterBtn.setPreferredSize(new Dimension(80, 32));
        resetFilterBtn.addActionListener(e -> {
            searchField.setText("");
            filterCategoryBox.setSelectedIndex(0);
            loadTableData();
        });

        filterBar.add(searchLbl);
        filterBar.add(searchField);
        filterBar.add(catLbl);
        filterBar.add(filterCategoryBox);
        filterBar.add(searchBtn);
        filterBar.add(resetFilterBtn);
        centerPanel.add(filterBar, BorderLayout.NORTH);

        // Expense Table Setup
        String[] columnNames = {"ID", "Title", "Amount", "Category", "Date", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table cells read-only; editing handled via Update dialog
            }
        };

        expenseTable = new JTable(tableModel);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.styleTable(expenseTable);

        // Specific Column Widths & Alignments
        expenseTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        expenseTable.getColumnModel().getColumn(1).setPreferredWidth(170);  // Title
        expenseTable.getColumnModel().getColumn(2).setPreferredWidth(110);  // Amount
        expenseTable.getColumnModel().getColumn(3).setPreferredWidth(110);  // Category
        expenseTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Date
        expenseTable.getColumnModel().getColumn(5).setPreferredWidth(230);  // Description

        // Center align ID, Category, Date
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        expenseTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        expenseTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        expenseTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        // Right align Amount
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        expenseTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

        JScrollPane tableScroll = new JScrollPane(expenseTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        tableScroll.getViewport().setBackground(Color.WHITE);
        centerPanel.add(tableScroll, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Action Bar ---
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);

        countLabel = new JLabel("Showing 0 expenses");
        countLabel.setFont(UITheme.FONT_SUBTITLE);
        countLabel.setForeground(UITheme.TEXT_MUTED);
        bottomBar.add(countLabel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton updateBtn = UITheme.createPrimaryButton("Update Expense");
        updateBtn.addActionListener(e -> handleUpdateExpense());

        JButton deleteBtn = UITheme.createDangerButton("Delete Expense");
        deleteBtn.addActionListener(e -> handleDeleteExpense());

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> {
            loadTableData();
            if (dashboard != null) {
                dashboard.refreshAllPanels();
            }
        });

        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);
        bottomBar.add(btnPanel, BorderLayout.EAST);

        add(bottomBar, BorderLayout.SOUTH);
    }

    /**
     * Loads all records from ExpenseManager into the table.
     */
    public void loadTableData() {
        populateTable(expenseManager.getAllExpenses());
    }

    /**
     * Filters expenses based on user input in search field and category dropdown.
     */
    private void applyFilter() {
        String keyword = searchField.getText().trim();
        String category = (String) filterCategoryBox.getSelectedItem();
        ArrayList<Expense> filtered = expenseManager.filterExpenses(keyword, category);
        populateTable(filtered);
    }

    private void populateTable(ArrayList<Expense> list) {
        tableModel.setRowCount(0);
        double totalSum = 0;
        for (Expense exp : list) {
            totalSum += exp.getAmount();
            tableModel.addRow(new Object[]{
                    exp.getId(),
                    exp.getTitle(),
                    UITheme.formatCurrency(exp.getAmount()),
                    exp.getCategory(),
                    exp.getDate(),
                    exp.getDescription().isEmpty() ? "-" : exp.getDescription()
            });
        }
        countLabel.setText("Showing " + list.size() + " record(s)  |  Total: " + UITheme.formatCurrency(totalSum));
    }

    /**
     * Opens modal update dialog for the selected row.
     */
    private void handleUpdateExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Please select an expense from the table first.", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int expenseId = (int) tableModel.getValueAt(selectedRow, 0);
        Expense exp = expenseManager.getExpenseById(expenseId);
        if (exp == null) {
            JOptionPane.showMessageDialog(this, "Expense not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show update dialog
        showUpdateDialog(exp);
    }

    private void showUpdateDialog(Expense exp) {
        JDialog dialog = new JDialog(dashboard, "Update Expense (ID: #" + exp.getId() + ")", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(480, 480);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(UITheme.BG_MAIN);
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField editTitleField = new JTextField(exp.getTitle());
        JTextField editAmountField = new JTextField(String.valueOf(exp.getAmount()));
        String[] categories = {"Food", "Travel", "Shopping", "Bills", "Education", "Entertainment", "Other"};
        JComboBox<String> editCategoryBox = new JComboBox<>(categories);
        editCategoryBox.setSelectedItem(exp.getCategory());
        JTextField editDateField = new JTextField(exp.getDate());
        JTextArea editDescArea = new JTextArea(exp.getDescription(), 3, 20);
        editDescArea.setLineWrap(true);
        editDescArea.setWrapStyleWord(true);

        // Add fields
        gbc.gridx = 0; gbc.gridy = 0;
        content.add(new JLabel("Title:"), gbc);
        gbc.gridy = 1;
        content.add(editTitleField, gbc);

        gbc.gridy = 2;
        content.add(new JLabel("Amount (₹):"), gbc);
        gbc.gridy = 3;
        content.add(editAmountField, gbc);

        gbc.gridy = 4;
        content.add(new JLabel("Category:"), gbc);
        gbc.gridy = 5;
        content.add(editCategoryBox, gbc);

        gbc.gridy = 6;
        content.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridy = 7;
        content.add(editDateField, gbc);

        gbc.gridy = 8;
        content.add(new JLabel("Description:"), gbc);
        gbc.gridy = 9;
        content.add(new JScrollPane(editDescArea), gbc);

        // Dialog buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setOpaque(false);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UITheme.createPrimaryButton("Save Changes");
        saveBtn.addActionListener(e -> {
            String newTitle = editTitleField.getText().trim();
            String newAmountStr = editAmountField.getText().trim();
            String newCategory = (String) editCategoryBox.getSelectedItem();
            String newDate = editDateField.getText().trim();
            String newDesc = editDescArea.getText().trim();

            if (newTitle.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Title cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double newAmount;
            try {
                newAmount = Double.parseDouble(newAmountStr);
                if (newAmount <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Amount must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid amount entered.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                LocalDate.parse(newDate);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use YYYY-MM-DD.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Perform update
            boolean updated = expenseManager.updateExpense(exp.getId(), newTitle, newAmount, newCategory, newDate, newDesc);
            if (updated) {
                JOptionPane.showMessageDialog(dialog, "Expense updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadTableData();
                if (dashboard != null) {
                    dashboard.refreshAllPanels();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update expense.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Handles expense deletion with confirmation.
     */
    private void handleDeleteExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Please select an expense from the table first.", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int expenseId = (int) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);
        String amount = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this expense?\n\nID: #" + expenseId + 
                "\nTitle: " + title + "\nAmount: " + amount, 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = expenseManager.deleteExpense(expenseId);
            if (deleted) {
                JOptionPane.showMessageDialog(this, "Expense deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
                if (dashboard != null) {
                    dashboard.refreshAllPanels();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting expense record.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

