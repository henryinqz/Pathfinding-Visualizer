import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); // Fixes Mac devices showing native JComponent styles (and making text not visible)
        } catch (Exception e) {
            e.printStackTrace();
        }

        GUI.frame = new JFrame("Pathfinding Visualizer"); // Create JFrame object titled "Pathfinding Visualizer"
        GUI.setPanel(new Board().getPanel()); // Set frame to board panel
        GUI.frame.setLocationRelativeTo(null); // Open frame in the center of screen
    }
}
