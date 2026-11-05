import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * SummaryPanel.java
 * Displays comprehensive dynamic analytics for expenses:
 * - Total spent, total transactions, average spending, highest expense.
 * - Category-wise breakdown with percentage calculations.
 * - Monthly expenditure summary.
 * All values are computed dynamically from ExpenseManager.
 */
public class SummaryPanel extends JPanel {
    private ExpenseManager expenseManager;

    private JLabel totalSpentValue;
    private JLabel totalTransValue;
    private JLabel avgExpenseValue;
    private JLabel highestExpenseValue;

    private DefaultTableModel categoryTableModel;
    private DefaultTableModel monthlyTableModel;

    public SummaryPanel(ExpenseManager expenseManager) {
        this.expenseManager = expenseManager;

        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        initComponents();
        refreshSummaryData();
    }

    private void initComponents() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Expense Summary & Analytics");
        titleLabel.setFont(UITheme.FONT_TITLE_LARGE);
        titleLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel subtitleLabel = new JLabel("Dynamic breakdown of your expenditures across categories and months");
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh Analytics");
        refreshBtn.addActionListener(e -> refreshSummaryData());

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- Center Scrollable Content ---
        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setOpaque(false);

        // 1. Metric Cards Grid
        JPanel cardsGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        cardsGrid.setOpaque(false);

        totalSpentValue = new JLabel("₹ 0.00");
        totalTransValue = new JLabel("0");
        avgExpenseValue = new JLabel("₹ 0.00");
        highestExpenseValue = new JLabel("None");

        cardsGrid.add(createKpiCard("TOTAL SPENT", totalSpentValue, "Cumulative lifetime expenses", UITheme.PRIMARY));
        cardsGrid.add(createKpiCard("TOTAL TRANSACTIONS", totalTransValue, "Number of recorded entries", UITheme.ACCENT_BLUE));
        cardsGrid.add(createKpiCard("AVERAGE EXPENSE", avgExpenseValue, "Mean spent per transaction", UITheme.SUCCESS));
        cardsGrid.add(createKpiCard("HIGHEST EXPENSE", highestExpenseValue, "Largest single purchase", UITheme.ACCENT_PURPLE));

        mainContent.add(cardsGrid, BorderLayout.NORTH);

        // 2. Analytics Tables Grid (Category Breakdown + Monthly Summary side by side)
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        tablesPanel.setOpaque(false);

        // Category Table
        RoundedPanel catCard = new RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER);
        catCard.setLayout(new BorderLayout(0, 10));
        catCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel catHeader = new JLabel("Category-Wise Spending");
        catHeader.setFont(UITheme.FONT_TITLE);
        catHeader.setForeground(UITheme.TEXT_MAIN);
        catCard.add(catHeader, BorderLayout.NORTH);

        String[] catCols = {"Category", "Total Spent", "Share (%)", "Count"};
        categoryTableModel = new DefaultTableModel(catCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable catTable = new JTable(categoryTableModel);
        UITheme.styleTable(catTable);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        catTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        catTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        catTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        JScrollPane catScroll = new JScrollPane(catTable);
        catScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        catScroll.getViewport().setBackground(Color.WHITE);
        catCard.add(catScroll, BorderLayout.CENTER);

        // Monthly Summary Table
        RoundedPanel monthCard = new RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER);
        monthCard.setLayout(new BorderLayout(0, 10));
        monthCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel monthHeader = new JLabel("Monthly Expense Summary");
        monthHeader.setFont(UITheme.FONT_TITLE);
        monthHeader.setForeground(UITheme.TEXT_MAIN);
        monthCard.add(monthHeader, BorderLayout.NORTH);

        String[] monthCols = {"Month (YYYY-MM)", "Total Spent", "Count", "Avg / Expense"};
        monthlyTableModel = new DefaultTableModel(monthCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable monthTable = new JTable(monthlyTableModel);
        UITheme.styleTable(monthTable);

        monthTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        monthTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        monthTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        JScrollPane monthScroll = new JScrollPane(monthTable);
        monthScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        monthScroll.getViewport().setBackground(Color.WHITE);
        monthCard.add(monthScroll, BorderLayout.CENTER);

        tablesPanel.add(catCard);
        tablesPanel.add(monthCard);

        mainContent.add(tablesPanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, String subtitle, Color topColor) {
        RoundedPanel card = new RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER);
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // Top decorative accent line
        JPanel accentBar = new JPanel();
        accentBar.setBackground(topColor);
        accentBar.setPreferredSize(new Dimension(100, 3));
        card.add(accentBar, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(3, 1, 0, 4));
        body.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SMALL);
        titleLbl.setForeground(UITheme.TEXT_MUTED);

        valueLabel.setFont(UITheme.FONT_CARD_VALUE);
        valueLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(UITheme.TEXT_MUTED);

        body.add(titleLbl);
        body.add(valueLabel);
        body.add(subLbl);
        card.add(body, BorderLayout.CENTER);

        return card;
    }

    /**
     * Recalculates and updates all analytical data fields and tables dynamically.
     */
    public void refreshSummaryData() {
        double totalSpent = expenseManager.getTotalExpenses();
        int totalTrans = expenseManager.getTotalTransactions();
        double avgExpense = expenseManager.getAverageExpense();
        Expense highest = expenseManager.getHighestExpense();

        // Update KPI Cards
        totalSpentValue.setText(UITheme.formatCurrency(totalSpent));
        totalTransValue.setText(String.valueOf(totalTrans));
        avgExpenseValue.setText(UITheme.formatCurrency(avgExpense));

        if (highest != null) {
            highestExpenseValue.setText(UITheme.formatCurrency(highest.getAmount()));
            highestExpenseValue.setToolTipText(highest.getTitle() + " (" + highest.getCategory() + ")");
        } else {
            highestExpenseValue.setText("₹ 0.00");
        }

        // 1. Update Category Breakdown Table
        categoryTableModel.setRowCount(0);
        Map<String, Double> categoryTotals = expenseManager.getCategoryWiseTotals();
        Map<String, Integer> categoryCounts = expenseManager.getCategoryWiseCounts();

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            String category = entry.getKey();
            double catAmount = entry.getValue();
            int count = categoryCounts.getOrDefault(category, 0);
            double percentage = (totalSpent > 0) ? (catAmount / totalSpent) * 100.0 : 0.0;

            categoryTableModel.addRow(new Object[]{
                    category,
                    UITheme.formatCurrency(catAmount),
                    String.format("%.1f%%", percentage),
                    count
            });
        }

        // 2. Update Monthly Breakdown Table
        monthlyTableModel.setRowCount(0);
        Map<String, Double> monthlyTotals = expenseManager.getMonthlyTotals();
        Map<String, Integer> monthlyCounts = expenseManager.getMonthlyCounts();

        if (monthlyTotals.isEmpty()) {
            monthlyTableModel.addRow(new Object[]{"No Data", "₹ 0.00", 0, "₹ 0.00"});
        } else {
            for (Map.Entry<String, Double> entry : monthlyTotals.entrySet()) {
                String month = entry.getKey();
                double monthAmount = entry.getValue();
                int count = monthlyCounts.getOrDefault(month, 0);
                double monthAvg = (count > 0) ? (monthAmount / count) : 0.0;

                monthlyTableModel.addRow(new Object[]{
                        month,
                        UITheme.formatCurrency(monthAmount),
                        count,
                        UITheme.formatCurrency(monthAvg)
                });
            }
        }
    }
}

