import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Board extends MouseAdapter implements ActionListener {
    // PROPERTIES
    BoardPanel boardPanel = new BoardPanel(); // Create new boardPanel JPanel object
    static Node[][] grid;
    static int gridWidth, nodeSideLength;

    // Draw tools
    JRadioButton butStart = new JRadioButton("Start node", true);
    JRadioButton butEnd = new JRadioButton("End node");
    JRadioButton butBarrier = new JRadioButton("Barrier node");

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
    public void mousePressed(MouseEvent evt) {
        int mouseX = evt.getX(), mouseY = evt.getY();
        if (mouseX >= GUI.MENU_WIDTH && mouseX < GUI.FRAME_WIDTH && mouseY >= 0 && mouseY < GUI.FRAME_HEIGHT && grid != null) { // Mouse pointer is within grid (not in menu bar)
            int gridX = (mouseX-GUI.MENU_WIDTH) / nodeSideLength;
            int gridY = mouseY / nodeSideLength;

            if (SwingUtilities.isLeftMouseButton(evt) && grid[gridY][gridX].isEmpty()) { // Left click (draw)
                if (butStart.isSelected()) { // TODO: limit only one start & end node
                    grid[gridY][gridX].setStart();
                } else if (butEnd.isSelected()) {
                    grid[gridY][gridX].setEnd();
                } else if (butBarrier.isSelected()) {
                    grid[gridY][gridX].setBarrier();
                }
            } else if (SwingUtilities.isRightMouseButton(evt)) { // Right click (erase)
                grid[gridY][gridX].setEmpty();
            }
        }
    }
    public void mouseDragged(MouseEvent evt) {
        int mouseX = evt.getX(), mouseY = evt.getY();
        if (mouseX >= GUI.MENU_WIDTH && mouseX < GUI.FRAME_WIDTH && mouseY >= 0 && mouseY < GUI.FRAME_HEIGHT && grid != null) { // Mouse pointer is within grid (not in menu bar)
            int gridX = (mouseX-GUI.MENU_WIDTH) / nodeSideLength;
            int gridY = mouseY / nodeSideLength;

            if (SwingUtilities.isLeftMouseButton(evt) && grid[gridY][gridX].isEmpty()) { // Left click (draw)
                if (butStart.isSelected()) {
                    grid[gridY][gridX].setStart();
                } else if (butEnd.isSelected()) {
                    grid[gridY][gridX].setEnd();
                } else if (butBarrier.isSelected()) {
                    grid[gridY][gridX].setBarrier();
                }
            } else if (SwingUtilities.isRightMouseButton(evt)) { // Right click (erase)
                grid[gridY][gridX].setEmpty();
            }
        }
    }

    // CONSTRUCTOR
    public Board() {
        this.boardPanel.setPreferredSize(new Dimension(GUI.FRAME_WIDTH, GUI.FRAME_HEIGHT));
        this.boardPanel.setLayout(null);
        this.boardPanel.addMouseListener(this);
        this.boardPanel.addMouseMotionListener(this);
        //this.mainMenuPanel.setBackground(Color.DARK_GRAY);

        // Draw tool radio buttons
        this.boardPanel.add(this.butStart);
        this.butStart.setBounds(20,100,100,20);
        this.butStart.setFocusable(false);
        this.butStart.addActionListener(this);

        this.boardPanel.add(this.butEnd);
        this.butEnd.setBounds(20,100+20,100,20);
        this.butEnd.setFocusable(false);
        this.butEnd.addActionListener(this);

        this.boardPanel.add(this.butBarrier);
        this.butBarrier.setBounds(20,100+20+20,100,20);
        this.butBarrier.setFocusable(false);
        this.butBarrier.addActionListener(this);

        ButtonGroup drawTools = new ButtonGroup(); // Only allows one draw tool radio button to be pressed at a time
        drawTools.add(this.butStart);
        drawTools.add(this.butEnd);
        drawTools.add(this.butBarrier);

        generateGrid(25); // TODO: limit range from 8-40; default 25

        this.timerBoard.start(); // 60FPS timer
    }

} class BoardPanel extends JPanel {
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

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
