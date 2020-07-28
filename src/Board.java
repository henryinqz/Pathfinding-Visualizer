import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Board extends MouseAdapter implements ActionListener {
    // PROPERTIES
    BoardPanel boardPanel = new BoardPanel(); // Create new boardPanel JPanel object

    // Grid
    static Node[][] grid;
    static int gridWidth, nodeSideLength;
    private boolean startNodeExists = false;
    private boolean endNodeExists = false;

    // Draw tools
    JRadioButton butStart = new JRadioButton("Start node", true);
    JRadioButton butEnd = new JRadioButton("End node");
    JRadioButton butBarrier = new JRadioButton("Barrier node");

    Timer timerBoard = new Timer(1000 / 60, this); //60FPS

    // METHODS
    public JPanel getPanel() { // Return current panel
        return this.boardPanel;
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == timerBoard) this.boardPanel.repaint();
    }

    public void generateGrid(int width) { // Generate a square grid
        this.grid = new Node[width][width];
        for (int i=0; i < width; i++) { // Load Node grid/array
            for (int j=0; j < width; j++) {
                this.grid[i][j] = new Node(i,j);
            }
        }

        this.gridWidth = width;
        this.nodeSideLength = GUI.FRAME_HEIGHT/Board.grid.length;
    }

    // MouseListener/MouseMotionListener methods
    public void mousePressed(MouseEvent evt) {
        int mouseX = evt.getX(), mouseY = evt.getY();
        if (mouseX >= GUI.MENU_WIDTH && mouseX < GUI.FRAME_WIDTH && mouseY >= 0 && mouseY < GUI.FRAME_HEIGHT && this.grid != null) { // Mouse pointer is within grid (not in menu bar)
            int gridX = (mouseX-GUI.MENU_WIDTH) / this.nodeSideLength;
            int gridY = mouseY / this.nodeSideLength;

            if (SwingUtilities.isLeftMouseButton(evt) && this.grid[gridY][gridX].isEmpty()) { // Left click (draw)
                if (this.butStart.isSelected() && !this.startNodeExists) { // Checks which draw tool is selected. Also limits only one start/end node
                    this.grid[gridY][gridX].setStart();
                    this.startNodeExists = true;
                } else if (this.butEnd.isSelected() && !this.endNodeExists) {
                    this.grid[gridY][gridX].setEnd();
                    this.endNodeExists = true;
                } else if (this.butBarrier.isSelected()) {
                    this.grid[gridY][gridX].setBarrier();
                }
            } else if (SwingUtilities.isRightMouseButton(evt)) { // Right click (erase)
                if (this.grid[gridY][gridX].isStart()) { // Reset booleans that track if start/end node exists
                    this.startNodeExists = false;
                } else if (this.grid[gridY][gridX].isEnd()) {
                    this.endNodeExists = false;
                }
                this.grid[gridY][gridX].setEmpty();
            }
        }
    }
    public void mouseDragged(MouseEvent evt) {
        int mouseX = evt.getX(), mouseY = evt.getY();
        if (mouseX >= GUI.MENU_WIDTH && mouseX < GUI.FRAME_WIDTH && mouseY >= 0 && mouseY < GUI.FRAME_HEIGHT && this.grid != null) { // Mouse pointer is within grid (not in menu bar)
            int gridX = (mouseX-GUI.MENU_WIDTH) / this.nodeSideLength;
            int gridY = mouseY / this.nodeSideLength;

            if (SwingUtilities.isLeftMouseButton(evt) && this.grid[gridY][gridX].isEmpty()) { // Left click (draw)
                if (this.butStart.isSelected() && !this.startNodeExists) { // Checks which draw tool is selected. Also limits only one start/end node
                    this.grid[gridY][gridX].setStart();
                    this.startNodeExists = true;
                } else if (this.butEnd.isSelected() && !this.endNodeExists) {
                    this.grid[gridY][gridX].setEnd();
                    this.endNodeExists = true;
                } else if (this.butBarrier.isSelected()) {
                    this.grid[gridY][gridX].setBarrier();
                }
            } else if (SwingUtilities.isRightMouseButton(evt)) { // Right click (erase)
                if (this.grid[gridY][gridX].isStart()) { // Reset booleans that track if start/end node exists
                    this.startNodeExists = false;
                } else if (this.grid[gridY][gridX].isEnd()) {
                    this.endNodeExists = false;
                }
                this.grid[gridY][gridX].setEmpty();
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
