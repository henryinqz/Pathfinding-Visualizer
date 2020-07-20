import javax.swing.*;

public class GUI {
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
}
