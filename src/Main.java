import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Main {
    // PROPERTIES
    // Frame/menu constants
    public final static int MENU_WIDTH = 250;
    public final static int FRAME_HEIGHT = 800;
    public final static int FRAME_WIDTH = MENU_WIDTH + FRAME_HEIGHT;
    public final static int MENU_PADDING = 10; // 10px
    public static JFrame frame;

    // Fonts
    public final static Font FONT_TITLE = loadFont("Roboto-Bold",22),
            FONT_HEADER = loadFont("Roboto-Medium",14),
            FONT_SUBHEADER = loadFont("Roboto-Regular",18),
            FONT_NORMAL = loadFont("Roboto-Regular", 12),
            FONT_NORMAL_ITALICS = loadFont("Roboto-Italic", 12);

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
    public static void centerJButton(JButton button, int width) { // Center JButton within given width
        int center = width/2;
        int butWidth = button.getWidth();
        int centeredX = center - (butWidth/2);

        button.setLocation(centeredX, button.getY());
    }

    // MAIN METHOD
    public static void main(String[] args) {
        try {
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); // Fixes Mac devices showing native JComponent styles (and making text not visible)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // Modern Look & Feel UI

            // Set default font to FONT_NORMAL
            UIManager.put("Button.font", FONT_NORMAL);
            UIManager.put("ToggleButton.font", FONT_NORMAL);
            UIManager.put("RadioButton.font", FONT_NORMAL);
            UIManager.put("CheckBox.font", FONT_NORMAL);
            UIManager.put("ColorChooser.font", FONT_NORMAL);
            UIManager.put("ComboBox.font", FONT_NORMAL);
            UIManager.put("Label.font", FONT_NORMAL);
            UIManager.put("List.font", FONT_NORMAL);
            UIManager.put("MenuBar.font", FONT_NORMAL);
            UIManager.put("MenuItem.font", FONT_NORMAL);
            UIManager.put("RadioButtonMenuItem.font", FONT_NORMAL);
            UIManager.put("CheckBoxMenuItem.font", FONT_NORMAL);
            UIManager.put("Menu.font", FONT_NORMAL);
            UIManager.put("PopupMenu.font", FONT_NORMAL);
            UIManager.put("OptionPane.font", FONT_NORMAL);
            UIManager.put("Panel.font", FONT_NORMAL);
            UIManager.put("ProgressBar.font", FONT_NORMAL);
            UIManager.put("ScrollPane.font", FONT_NORMAL);
            UIManager.put("Viewport.font", FONT_NORMAL);
            UIManager.put("TabbedPane.font", FONT_NORMAL);
            UIManager.put("Table.font", FONT_NORMAL);
            UIManager.put("TableHeader.font", FONT_NORMAL);
            UIManager.put("TextField.font", FONT_NORMAL);
            UIManager.put("PasswordField.font", FONT_NORMAL);
            UIManager.put("TextArea.font", FONT_NORMAL);
            UIManager.put("TextPane.font", FONT_NORMAL);
            UIManager.put("EditorPane.font", FONT_NORMAL);
            UIManager.put("TitledBorder.font", FONT_NORMAL);
            UIManager.put("ToolBar.font", FONT_NORMAL);
            UIManager.put("ToolTip.font", FONT_NORMAL);
            UIManager.put("Tree.font", FONT_NORMAL);


        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Pathfinding Visualizer"); // Create JFrame object titled "Pathfinding Visualizer"
        setPanel(new Board().getPanel()); // Set frame to board panel
        frame.setLocationRelativeTo(null); // Open frame in the center of screen
    }

}
