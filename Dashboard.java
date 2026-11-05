import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Dashboard.java
 * Main application window and navigation container.
 * Features a modern sidebar navigation bar and a multi-view CardLayout:
 * 1. Dashboard Overview (KPI Cards, Recent Transactions, Quick Actions)
 * 2. Add Expense Panel
 * 3. View Expenses Panel
 * 4. Expense Summary Panel
 */
public class Dashboard extends JFrame {
    private ExpenseManager expenseManager;

    // Card Layout Container
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    // Sub-Panels
    private JPanel dashboardHomePanel;
    private AddExpensePanel addExpensePanel;
    private ViewExpensesPanel viewExpensesPanel;
    private SummaryPanel summaryPanel;

    // Dashboard Overview Widgets
    private JLabel totalExpensesLabel;
    private JLabel totalTransactionsLabel;
    private JLabel topCategoryLabel;
    private DefaultTableModel recentTableModel;
    private JTable recentTable;

    // Sidebar Navigation Buttons
    private JButton btnNavDashboard;
    private JButton btnNavAdd;
    private JButton btnNavView;
    private JButton btnNavSummary;
    private JButton currentActiveBtn;

    public Dashboard() {
        // Initialize Business Logic Layer
        this.expenseManager = new ExpenseManager();

        setTitle("Personal Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 700);
        setMinimumSize(new Dimension(920, 620));
        setLocationRelativeTo(null);

        initComponents();
        refreshAllPanels();
    }

    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UITheme.BG_MAIN);

        // 1. Sidebar Navigation
        JPanel sidebar = createSidebar();
        rootPanel.add(sidebar, BorderLayout.WEST);

        // 2. Center Card Panel
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(UITheme.BG_MAIN);

        // Create Sub-Panels
        dashboardHomePanel = createDashboardHomeView();
        addExpensePanel = new AddExpensePanel(expenseManager, this);
        viewExpensesPanel = new ViewExpensesPanel(expenseManager, this);
        summaryPanel = new SummaryPanel(expenseManager);

        mainContentPanel.add(dashboardHomePanel, "DASHBOARD");
        mainContentPanel.add(addExpensePanel, "ADD");
        mainContentPanel.add(viewExpensesPanel, "VIEW");
        mainContentPanel.add(summaryPanel, "SUMMARY");

        rootPanel.add(mainContentPanel, BorderLayout.CENTER);
        add(rootPanel);

        // Default to Dashboard view
        showPanel("DASHBOARD", btnNavDashboard);
    }

    /**
     * Constructs the dark modern sidebar navigation panel.
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(240, 700));
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 16, 24, 16));

        // Brand / Title Area
        JLabel appIcon = new JLabel("💳 EXPENSE TRACKER");
        appIcon.setFont(UITheme.FONT_TITLE);
        appIcon.setForeground(Color.WHITE);
        appIcon.setAlignmentX(LEFT_ALIGNMENT);

        JLabel appSubtitle = new JLabel("Personal Finance Manager");
        appSubtitle.setFont(UITheme.FONT_SMALL);
        appSubtitle.setForeground(new Color(148, 163, 184));
        appSubtitle.setAlignmentX(LEFT_ALIGNMENT);

        sidebar.add(appIcon);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(appSubtitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        // Navigation Buttons
        btnNavDashboard = createSidebarNavButton("📊  Dashboard");
        btnNavAdd = createSidebarNavButton("➕  Add Expense");
        btnNavView = createSidebarNavButton("📋  View Expenses");
        btnNavSummary = createSidebarNavButton("📈  Expense Summary");

        btnNavDashboard.addActionListener(e -> showPanel("DASHBOARD", btnNavDashboard));
        btnNavAdd.addActionListener(e -> showPanel("ADD", btnNavAdd));
        btnNavView.addActionListener(e -> showPanel("VIEW", btnNavView));
        btnNavSummary.addActionListener(e -> showPanel("SUMMARY", btnNavSummary));

        sidebar.add(btnNavDashboard);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnNavAdd);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnNavView);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnNavSummary);

        // Spacer pushes system card to bottom
        sidebar.add(Box.createVerticalGlue());

        // Semester Project Info Card
        RoundedPanel infoCard = new RoundedPanel(8, new Color(30, 41, 59), new Color(51, 65, 85));
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        infoCard.setMaximumSize(new Dimension(210, 85));
        infoCard.setAlignmentX(LEFT_ALIGNMENT);

        JLabel infoTitle = new JLabel("Semester Project");
        infoTitle.setFont(UITheme.FONT_BOLD);
        infoTitle.setForeground(UITheme.TEXT_LIGHT);

        JLabel infoSub1 = new JLabel("Core Java • Swing GUI");
        infoSub1.setFont(UITheme.FONT_SMALL);
        infoSub1.setForeground(new Color(148, 163, 184));

        JLabel infoSub2 = new JLabel("Storage: expenses.txt");
        infoSub2.setFont(UITheme.FONT_SMALL);
        infoSub2.setForeground(new Color(56, 189, 248));

        infoCard.add(infoTitle);
        infoCard.add(Box.createRigidArea(new Dimension(0, 4)));
        infoCard.add(infoSub1);
        infoCard.add(infoSub2);

        sidebar.add(infoCard);

        return sidebar;
    }

    private JButton createSidebarNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BOLD);
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(UITheme.BG_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(JButton.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(210, 42));
        btn.setPreferredSize(new Dimension(210, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != currentActiveBtn) {
                    btn.setBackground(new Color(30, 41, 59));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != currentActiveBtn) {
                    btn.setBackground(UITheme.BG_SIDEBAR);
                }
            }
        });

        return btn;
    }

    /**
     * Builds the main Dashboard overview screen with KPI Cards and Recent Transactions.
     */
    private JPanel createDashboardHomeView() {
        JPanel homePanel = new JPanel(new BorderLayout(0, 20));
        homePanel.setBackground(UITheme.BG_MAIN);
        homePanel.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        // 1. Welcome Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel welcomeTitle = new JLabel("Welcome to Personal Expense Tracker");
        welcomeTitle.setFont(UITheme.FONT_TITLE_LARGE);
        welcomeTitle.setForeground(UITheme.TEXT_MAIN);

        String todayFormatted = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        JLabel dateLabel = new JLabel("Today: " + todayFormatted + "  •  Track your everyday expenses easily");
        dateLabel.setFont(UITheme.FONT_SUBTITLE);
        dateLabel.setForeground(UITheme.TEXT_MUTED);

        headerPanel.add(welcomeTitle, BorderLayout.NORTH);
        headerPanel.add(dateLabel, BorderLayout.SOUTH);
        homePanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Center Content Area (Cards + Recent Table)
        JPanel centerArea = new JPanel(new BorderLayout(0, 20));
        centerArea.setOpaque(false);

        // 3 Cards: Total Expenses, Total Transactions, Top Spending Category
        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 18, 0));
        cardsRow.setOpaque(false);

        totalExpensesLabel = new JLabel("₹ 0.00");
        totalTransactionsLabel = new JLabel("0");
        topCategoryLabel = new JLabel("None");

        cardsRow.add(createDashboardCard("TOTAL EXPENSES", totalExpensesLabel, "All recorded expenditure", UITheme.PRIMARY));
        cardsRow.add(createDashboardCard("TOTAL TRANSACTIONS", totalTransactionsLabel, "Total expense entries", UITheme.ACCENT_BLUE));
        cardsRow.add(createDashboardCard("TOP CATEGORY", topCategoryLabel, "Highest category expense", UITheme.ACCENT_PURPLE));

        centerArea.add(cardsRow, BorderLayout.NORTH);

        // Recent Transactions Section
        RoundedPanel recentSection = new RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER);
        recentSection.setLayout(new BorderLayout(0, 12));
        recentSection.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);

        JLabel recentTitle = new JLabel("Recent Transactions (Latest 5)");
        recentTitle.setFont(UITheme.FONT_TITLE);
        recentTitle.setForeground(UITheme.TEXT_MAIN);

        JButton viewAllBtn = UITheme.createSecondaryButton("View All Expenses →");
        viewAllBtn.addActionListener(e -> showPanel("VIEW", btnNavView));

        recentHeader.add(recentTitle, BorderLayout.WEST);
        recentHeader.add(viewAllBtn, BorderLayout.EAST);
        recentSection.add(recentHeader, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Title", "Amount", "Category", "Date"};
        recentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        recentTable = new JTable(recentTableModel);
        UITheme.styleTable(recentTable);

        // Center ID, Category, Date
        DefaultTableCellRenderer centerRend = new DefaultTableCellRenderer();
        centerRend.setHorizontalAlignment(JLabel.CENTER);
        recentTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        recentTable.getColumnModel().getColumn(0).setCellRenderer(centerRend);
        recentTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        recentTable.getColumnModel().getColumn(3).setCellRenderer(centerRend);
        recentTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        recentTable.getColumnModel().getColumn(4).setCellRenderer(centerRend);

        // Right align Amount
        DefaultTableCellRenderer rightRend = new DefaultTableCellRenderer();
        rightRend.setHorizontalAlignment(JLabel.RIGHT);
        recentTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        recentTable.getColumnModel().getColumn(2).setCellRenderer(rightRend);

        JScrollPane recentScroll = new JScrollPane(recentTable);
        recentScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        recentScroll.getViewport().setBackground(Color.WHITE);
        recentSection.add(recentScroll, BorderLayout.CENTER);

        // Quick Action Row below Recent Table
        JPanel quickActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        quickActionPanel.setOpaque(false);

        JButton quickAddBtn = UITheme.createPrimaryButton("+ Add New Expense");
        quickAddBtn.addActionListener(e -> showPanel("ADD", btnNavAdd));

        quickActionPanel.add(quickAddBtn);
        recentSection.add(quickActionPanel, BorderLayout.SOUTH);

        centerArea.add(recentSection, BorderLayout.CENTER);
        homePanel.add(centerArea, BorderLayout.CENTER);

        return homePanel;
    }

    private JPanel createDashboardCard(String title, JLabel valueLabel, String subtitle, Color topBarColor) {
        RoundedPanel card = new RoundedPanel(12, UITheme.CARD_BG, UITheme.BORDER);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JPanel topBar = new JPanel();
        topBar.setBackground(topBarColor);
        topBar.setPreferredSize(new Dimension(100, 4));
        card.add(topBar, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(3, 1, 0, 4));
        content.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SMALL);
        titleLbl.setForeground(UITheme.TEXT_MUTED);

        valueLabel.setFont(UITheme.FONT_CARD_VALUE);
        valueLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(UITheme.TEXT_MUTED);

        content.add(titleLbl);
        content.add(valueLabel);
        content.add(subLbl);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    /**
     * Switches active panel in CardLayout and updates sidebar styling.
     */
    public void showPanel(String cardName, JButton activeBtn) {
        cardLayout.show(mainContentPanel, cardName);

        // Reset previous active button
        if (currentActiveBtn != null) {
            currentActiveBtn.setBackground(UITheme.BG_SIDEBAR);
            currentActiveBtn.setForeground(new Color(203, 213, 225));
        }

        // Highlight new active button
        if (activeBtn != null) {
            currentActiveBtn = activeBtn;
            currentActiveBtn.setBackground(UITheme.PRIMARY);
            currentActiveBtn.setForeground(Color.WHITE);
        }
    }

    /**
     * Refreshes all data across Dashboard Overview, View Table, and Summary.
     */
    public void refreshAllPanels() {
        // 1. Refresh Dashboard Overview Cards
        double totalExpenses = expenseManager.getTotalExpenses();
        int totalTransactions = expenseManager.getTotalTransactions();

        totalExpensesLabel.setText(UITheme.formatCurrency(totalExpenses));
        totalTransactionsLabel.setText(String.valueOf(totalTransactions));

        // Find top category
        Map<String, Double> categoryTotals = expenseManager.getCategoryWiseTotals();
        String topCategory = "None";
        double maxCategoryAmount = 0.0;
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            if (entry.getValue() > maxCategoryAmount) {
                maxCategoryAmount = entry.getValue();
                topCategory = entry.getKey() + " (" + UITheme.formatCurrency(maxCategoryAmount) + ")";
            }
        }
        topCategoryLabel.setText(topCategory);

        // 2. Refresh Recent Transactions Table (latest 5)
        recentTableModel.setRowCount(0);
        ArrayList<Expense> recent = expenseManager.getRecentExpenses(5);
        for (Expense exp : recent) {
            recentTableModel.addRow(new Object[]{
                    exp.getId(),
                    exp.getTitle(),
                    UITheme.formatCurrency(exp.getAmount()),
                    exp.getCategory(),
                    exp.getDate()
            });
        }

        // 3. Refresh View Expenses Table
        if (viewExpensesPanel != null) {
            viewExpensesPanel.loadTableData();
        }

        // 4. Refresh Summary Panel
        if (summaryPanel != null) {
            summaryPanel.refreshSummaryData();
        }
    }
}

