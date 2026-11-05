import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * UITheme.java
 * Centralizes UI constants, colors, typography, and styling helper methods.
 * Provides modern visual styling across all panels using pure Swing and AWT.
 */
public class UITheme {

    // --- Color Palette (Indigo / Blue Theme) ---
    public static final Color PRIMARY = new Color(67, 56, 202);          // Indigo 700
    public static final Color PRIMARY_HOVER = new Color(55, 48, 163);    // Indigo 800
    public static final Color PRIMARY_LIGHT = new Color(238, 242, 255);  // Indigo 50
    public static final Color ACCENT_BLUE = new Color(37, 99, 235);      // Blue 600
    public static final Color ACCENT_PURPLE = new Color(124, 58, 237);   // Purple 600
    public static final Color BG_MAIN = new Color(248, 250, 252);        // Slate 50
    public static final Color BG_SIDEBAR = new Color(24, 30, 49);        // Dark Navy/Slate for sidebar
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_MAIN = new Color(15, 23, 42);         // Slate 900
    public static final Color TEXT_MUTED = new Color(100, 116, 139);     // Slate 500
    public static final Color TEXT_LIGHT = new Color(241, 245, 249);     // Slate 100
    public static final Color BORDER = new Color(226, 232, 240);         // Slate 200
    public static final Color SUCCESS = new Color(16, 185, 129);         // Emerald 500
    public static final Color DANGER = new Color(239, 68, 68);           // Red 500
    public static final Color WARNING = new Color(245, 158, 11);         // Amber 500

    // --- Typography (Clean Segoe UI / SansSerif) ---
    public static final Font FONT_TITLE_LARGE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_CARD_VALUE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    // Number format for currency display
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");

    /**
     * Formats a double amount as currency: e.g. "₹ 1,250.00"
     */
    public static String formatCurrency(double amount) {
        return "₹ " + CURRENCY_FORMAT.format(amount);
    }

    /**
     * Creates a styled modern button with rounded feel, custom colors, and hover effects.
     */
    public static JButton createButton(String text, Color bg, Color fg, Color hoverBg) {
        JButton button = new JButton(text);
        button.setFont(FONT_BOLD);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bg);
            }
        });

        return button;
    }

    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY, Color.WHITE, PRIMARY_HOVER);
    }

    public static JButton createSecondaryButton(String text) {
        JButton button = createButton(text, new Color(241, 245, 249), TEXT_MAIN, new Color(226, 232, 240));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        return button;
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, DANGER, Color.WHITE, new Color(220, 38, 38));
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, SUCCESS, Color.WHITE, new Color(5, 150, 105));
    }

    /**
     * Styles a JTable with a clean modern appearance:
     * - Custom header background and font
     * - Comfortable row height
     * - Alternating row colors
     * - Centered dates and IDs
     * - Right-aligned amounts
     */
    public static void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(FONT_REGULAR);
        table.setForeground(TEXT_MAIN);
        table.setGridColor(new Color(241, 245, 249));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(PRIMARY);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(new Color(51, 65, 85));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setPreferredSize(new Dimension(header.getWidth(), 36));

        // Default cell renderer with alternating row colors
        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, defaultRenderer);
    }
}

