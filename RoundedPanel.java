import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * RoundedPanel.java
 * Custom JPanel supporting smooth anti-aliased rounded corners and optional borders.
 * Enhances standard Swing cards and containers with a clean, modern card aesthetic.
 */
public class RoundedPanel extends JPanel {
    private int cornerRadius;
    private Color backgroundColor;
    private Color borderColor;

    public RoundedPanel(int radius, Color bgColor, Color borderColor) {
        super();
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
        this.borderColor = borderColor;
        setOpaque(false);
    }

    public RoundedPanel(int radius, Color bgColor) {
        this(radius, bgColor, null);
    }

    public RoundedPanel(int radius) {
        this(radius, Color.WHITE, new Color(226, 232, 240));
    }

    public void setBackgroundColor(Color bg) {
        this.backgroundColor = bg;
        repaint();
    }

    public void setBorderColor(Color border) {
        this.borderColor = border;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2.setColor(backgroundColor != null ? backgroundColor : getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        // Optional Border
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}

