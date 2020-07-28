import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Board extends MouseAdapter implements ActionListener {
    // PROPERTIES
    private BoardPanel boardPanel = new BoardPanel(); // Create new boardPanel JPanel object

    // Grid
    public static Node[][] grid;
    public static int gridWidth, nodeSideLength;
    private Node startNode, endNode;
    //private boolean startNodeExists = false;
    //private boolean endNodeExists = false;

    // Draw tools
    private JRadioButton butStart = new JRadioButton("Start node", true);
    private JRadioButton butEnd = new JRadioButton("End node");
    private JRadioButton butBarrier = new JRadioButton("Barrier node");
    private JLabel labelErase = new JLabel("Right click to erase nodes");
    private JButton butClear = new JButton("Clear grid");

    private Timer timerBoard = new Timer(1000 / 60, this); // 60FPS

    // METHODS
    public JPanel getPanel() { // Return current panel
        return this.boardPanel;
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == this.timerBoard) { // 60FPS Timer
            this.boardPanel.repaint();
        } else if (evt.getSource() == this.butClear) {
            if (JOptionPane.showConfirmDialog(null, "Are you sure you want to clear the grid?", "Warning",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { // Confirm clear grid dialog. Yes option.
                generateGrid(gridWidth);
            } else { // No option
            }
        }
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

        this.startNode = null; // Reset start/end nodes
        this.endNode = null;
    }

    // MouseListener/MouseMotionListener methods
    public void mousePressed(MouseEvent evt) {
        int mouseX = evt.getX(), mouseY = evt.getY();
        if (mouseX >= GUI.MENU_WIDTH && mouseX < GUI.FRAME_WIDTH && mouseY >= 0 && mouseY < GUI.FRAME_HEIGHT && this.grid != null) { // Mouse pointer is within grid (not in menu bar)
            int gridX = (mouseX-GUI.MENU_WIDTH) / this.nodeSideLength;
            int gridY = mouseY / this.nodeSideLength;

            if (SwingUtilities.isLeftMouseButton(evt) && this.grid[gridY][gridX].isEmpty()) { // Left click (draw)
                if (this.butStart.isSelected() && this.startNode == null) { // Checks which draw tool is selected. Also limits only one start/end node
                    this.grid[gridY][gridX].setStart();
                    this.startNode = this.grid[gridY][gridX];
                } else if (this.butEnd.isSelected() && this.endNode == null) {
                    this.grid[gridY][gridX].setEnd();
                    this.endNode = this.grid[gridY][gridX];
                } else if (this.butBarrier.isSelected()) {
                    this.grid[gridY][gridX].setBarrier();
                }
            } else if (SwingUtilities.isRightMouseButton(evt)) { // Right click (erase)
                if (this.grid[gridY][gridX].isStart()) { // Reset booleans that track if start/end node exists
                    this.startNode = null;
                } else if (this.grid[gridY][gridX].isEnd()) {
                    this.endNode = null;
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
                if (this.butStart.isSelected() && this.startNode == null) { // Checks which draw tool is selected. Also limits only one start/end node
                    this.grid[gridY][gridX].setStart();
                    this.startNode = this.grid[gridY][gridX];
                } else if (this.butEnd.isSelected() && this.endNode == null) {
                    this.grid[gridY][gridX].setEnd();
                    this.endNode = this.grid[gridY][gridX];
                } else if (this.butBarrier.isSelected()) {
                    this.grid[gridY][gridX].setBarrier();
                }
            } else if (SwingUtilities.isRightMouseButton(evt)) { // Right click (erase)
                if (this.grid[gridY][gridX].isStart()) { // Reset booleans that track if start/end node exists
                    this.startNode = null;
                } else if (this.grid[gridY][gridX].isEnd()) {
                    this.endNode = null;
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
        //this.boardPanel.setBackground(Color.DARK_GRAY);

        // Draw tool radio buttons
        this.boardPanel.add(this.butStart);
        this.butStart.setBounds(20,100,100,20);
        this.butStart.setFocusable(false);
        this.butStart.addActionListener(this);

        this.boardPanel.add(this.butEnd);
        this.butEnd.setBounds(20,100+(20),100,20);
        this.butEnd.setFocusable(false);
        this.butEnd.addActionListener(this);

        this.boardPanel.add(this.butBarrier);
        this.butBarrier.setBounds(20,100+(20*2),100,20);
        this.butBarrier.setFocusable(false);
        this.butBarrier.addActionListener(this);

        this.boardPanel.add(this.labelErase);
        this.labelErase.setBounds(20,100+(20*3),150,20);

        this.boardPanel.add(this.butClear);
        this.butClear.setBounds(20,100+(20*5),100,40);
        this.butClear.setFocusable(false);
        this.butClear.addActionListener(this);

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
