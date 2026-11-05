import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main.java
 * Application entry point for the Personal Expense Tracker semester project.
 * Configures the Swing Look and Feel and initializes the main Dashboard window
 * on the Swing Event Dispatch Thread (EDT).
 */
public class Main {
    public static void main(String[] args) {
        // Run GUI initialization on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                // Apply the native system look and feel for a clean OS-integrated appearance
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.out.println("Note: Using default Swing Look and Feel (" + e.getMessage() + ")");
            }

            // Launch Dashboard Window
            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
        });
    }
}

