import javax.swing.*;
import java.awt.Font;
import java.io.File;

public class Main {
    // PROPERTIES
    public final static int MENU_WIDTH = 250;
    public final static int FRAME_HEIGHT = 800;
    public final static int FRAME_WIDTH = MENU_WIDTH + FRAME_HEIGHT;
    public static JFrame frame;

    // METHODS
    public static void setPanel (JPanel changePanel) { // Changes panel within GUI.theframe
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close game when window is closed
        frame.setContentPane(changePanel); // Set panel to specified panel
        frame.pack(); // Change frame size if needed to allow for panel
        frame.setResizable(false); // Disable resizing frame/window
        frame.setVisible(true); // Set visible
    }
    public static Font loadFont(String fontName, int fontSize) {
        Font font = null; // initialize font object
        try {
            font = Font.createFont(Font.PLAIN, new File("assets/font/" + fontName + ".ttf")).deriveFont(Font.PLAIN, fontSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return font;
    }


    public static void main(String[] args) { // Main method
        try {
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); // Fixes Mac devices showing native JComponent styles (and making text not visible)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Pathfinding Visualizer"); // Create JFrame object titled "Pathfinding Visualizer"
        setPanel(new Board().getPanel()); // Set frame to board panel
        frame.setLocationRelativeTo(null); // Open frame in the center of screen
    }

}
