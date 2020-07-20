import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Board implements ActionListener, MouseListener, MouseMotionListener {
    // PROPERTIES
    BoardPanel boardPanel = new BoardPanel(); // Create new boardPanel JPanel object
    public static Node[][] grid;
    public static int gridWidth, nodeSideLength;

    Timer timerBoard = new Timer(1000 / 60, this); //60FPS

    // METHODS
    public JPanel getPanel() { // Return current panel
        return boardPanel;
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == timerBoard) boardPanel.repaint();
    }

    public void generateGrid(int width) { // Generate a square grid
        grid = new Node[width][width];
        for (int i=0; i<width; i++) { // Load Node grid/array
            for (int j=0; j<width; j++) {
                grid[i][j] = new Node(i,j);
            }
        }

        gridWidth = width;
        nodeSideLength = GUI.FRAME_HEIGHT/Board.grid.length;
    }

    // MouseListener/MouseMotionListener methods
    public void mouseClicked(MouseEvent evt) {
    }
    public void mousePressed(MouseEvent evt) {
        int mouseX = evt.getX();
        int mouseY = evt.getY();
        if (mouseX >= GUI.MENU_WIDTH && mouseX <= GUI.FRAME_WIDTH && mouseY >= 0 && mouseY <= GUI.FRAME_HEIGHT && grid != null) { // Mouse pointer is within grid (not in menu bar)
            int gridX = (mouseX-GUI.MENU_WIDTH) / nodeSideLength;
            int gridY = mouseY / nodeSideLength;

            if (SwingUtilities.isLeftMouseButton(evt)) { // Left click (draw)
                grid[gridY][gridX].setBarrier();
            } else if (SwingUtilities.isRightMouseButton(evt)) { // Right click (erase)
                grid[gridY][gridX].setEmpty();
            }
        }
    }
    public void mouseReleased(MouseEvent evt) {
    }
    public void mouseEntered(MouseEvent evt) {
    }
    public void mouseExited(MouseEvent evt) {
    }
    public void mouseDragged(MouseEvent evt) {
        System.out.println("Dragged: " + evt.getButton());

        int mouseX = evt.getX();
        int mouseY = evt.getY();
        if (mouseX >= GUI.MENU_WIDTH && mouseX <= GUI.FRAME_WIDTH && mouseY >= 0 && mouseY <= GUI.FRAME_HEIGHT && grid != null) { // Mouse pointer is within grid (not in menu bar)
            int gridX = (mouseX-GUI.MENU_WIDTH) / nodeSideLength;
            int gridY = mouseY / nodeSideLength;

            if (SwingUtilities.isLeftMouseButton(evt)) { // Left click (draw)
                grid[gridY][gridX].setBarrier();
            } else if (SwingUtilities.isRightMouseButton(evt)) { // Right click (erase)
                grid[gridY][gridX].setEmpty();
            }
        }
    }
    public void mouseMoved(MouseEvent evt) {
    }

    // CONSTRUCTOR
    public Board() {
        this.boardPanel.setPreferredSize(new Dimension(GUI.FRAME_WIDTH, GUI.FRAME_HEIGHT));
        this.boardPanel.setLayout(null);
        //this.mainMenuPanel.setBackground(Color.DARK_GRAY);

        this.boardPanel.addMouseListener(this);
        this.boardPanel.addMouseMotionListener(this);

        generateGrid(25); // TODO: limit range from 8-40; default 25

        this.timerBoard.start(); // 60FPS timer
    }

} class BoardPanel extends JPanel {
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //int sideLength = GUI.FRAME_HEIGHT/Board.grid.length;
        drawNodes(g, Board.grid, Board.nodeSideLength);
        drawGrid(g, Board.grid, Board.nodeSideLength);
    }

    public void drawGrid(Graphics g, Node[][] grid, int sideLength) {
        for (int i=0; i<=grid.length; i++) { // Horizontal grid lines
            g.drawLine(GUI.MENU_WIDTH,i*sideLength, GUI.FRAME_WIDTH,i*sideLength);
        }
        for (int j=0; j<=grid[0].length; j++) { // Vertical grid lines
           g.drawLine(GUI.MENU_WIDTH+(j*sideLength), 0, GUI.MENU_WIDTH+(j*sideLength), GUI.FRAME_HEIGHT);
        }
    }
    public void drawNodes(Graphics g, Node[][] grid, int sideLength) {
        for (int i=0; i<grid.length; i++) {
            for (int j=0; j<grid[i].length; j++) {
                g.setColor(grid[i][j].getColor());
                g.fillRect(GUI.MENU_WIDTH+(j*sideLength), i*sideLength, sideLength, sideLength); // Draw node
            }
        }
        g.setColor(Color.BLACK);
    }
}
