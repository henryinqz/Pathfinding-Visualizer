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

    // Draw tools
    private JRadioButton rbStartNode = new JRadioButton("Start node", true);
    private JRadioButton rbEndNode = new JRadioButton("End node");
    private JRadioButton rbBarrierNode = new JRadioButton("Barrier node");
    private JLabel labelErase = new JLabel("Right click to erase nodes");
    private JButton butClearGrid = new JButton("Clear grid");

    // Algorithm types
    private JRadioButton rbBFS = new JRadioButton("Breadth-first search", true);
    private JRadioButton rbDFS = new JRadioButton("Depth-first search");
    private JRadioButton rbAStar = new JRadioButton("A* search algorithm");
    private JRadioButton rbDijkstra = new JRadioButton("Dijkstra's algorithm");
    private JButton butStartSearch = new JButton("Start pathfinding");

    private Timer timerBoard = new Timer(1000 / 60, this); // 60FPS Timer

    // METHODS
    public JPanel getPanel() { // Return current panel
        return this.boardPanel;
    }

    // Start/end node getters/setters
    public Node getStartNode() {
        return this.startNode;
    }
    public Node getEndNode() {
        return this.endNode;
    }
    public void setStartNode(Node startNode) {
        this.startNode = startNode;
        if (startNode == null) {
            this.rbStartNode.setText("Start node");
        } else {
            this.rbStartNode.setText("Start node: (" + startNode.getX() + ", " + startNode.getY() + ")"); // TODO: Consider changing to non-array x/y coordinates (+1 to each val)
        }
        updateStartSearchButton();
    }
    public void setEndNode(Node endNode) {
        this.endNode = endNode;
        if (endNode == null) {
            this.rbEndNode.setText("End node");
        } else {
            this.rbEndNode.setText("End node: (" + endNode.getX() + ", " + endNode.getY() + ")"); // TODO: Consider changing to non-array x/y coordinates (+1 to each val)
        }
        updateStartSearchButton();
    }
    public void updateStartSearchButton() {
        if (getStartNode() != null && getEndNode() != null) { // If there are start/end nodes set, allow start button to be pressed
            this.butStartSearch.setEnabled(true);
        } else {
            this.butStartSearch.setEnabled(false);
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

        setStartNode(null); // Reset start/end nodes
        setEndNode(null);
    }

    // Listener methods (ActionListener, MouseListener/MouseMotionListener/MouseAdapter)
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == this.timerBoard) { // 60FPS Timer
            this.boardPanel.repaint();
        } else if (evt.getSource() == this.butClearGrid) {
            if (JOptionPane.showConfirmDialog(null, "Are you sure you want to clear the grid?", "Warning",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { // Confirm clear grid dialog. Yes option.
                generateGrid(gridWidth);
            } else { // No option
            }
        } else if (evt.getSource() == this.butStartSearch) { // Start pathfinding TODO: finish methods
            if (this.rbBFS.isSelected()) { // Breadth-first search
            } else if (this.rbDFS.isSelected()) { // Depth-first search
            } else if (this.rbAStar.isSelected()) { // A* search algorithm
            } else if (this.rbDijkstra.isSelected()) { // Dijkstra's algorithm
            }
        }
    }
    public void mousePressed(MouseEvent evt) {
        int mouseX = evt.getX(), mouseY = evt.getY();
        if (mouseX >= GUI.MENU_WIDTH && mouseX < GUI.FRAME_WIDTH && mouseY >= 0 && mouseY < GUI.FRAME_HEIGHT && this.grid != null) { // Mouse pointer is within grid (not in menu bar)
            int gridX = (mouseX-GUI.MENU_WIDTH) / this.nodeSideLength;
            int gridY = mouseY / this.nodeSideLength;

            if (SwingUtilities.isLeftMouseButton(evt) && this.grid[gridY][gridX].isEmpty()) { // Left click (draw)
                if (this.rbStartNode.isSelected() && this.startNode == null) { // Checks which draw tool is selected. Also limits only one start/end node
                    this.grid[gridY][gridX].setStart();
                    setStartNode(this.grid[gridY][gridX]);
                } else if (this.rbEndNode.isSelected() && this.endNode == null) {
                    this.grid[gridY][gridX].setEnd();
                    setEndNode(this.grid[gridY][gridX]);
                } else if (this.rbBarrierNode.isSelected()) {
                    this.grid[gridY][gridX].setBarrier();
                }
            } else if (SwingUtilities.isRightMouseButton(evt)) { // Right click (erase)
                if (this.grid[gridY][gridX].isStart()) { // Reset start/end node if it exists
                    setStartNode(null);
                } else if (this.grid[gridY][gridX].isEnd()) {
                    setEndNode(null);
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
                if (this.rbStartNode.isSelected() && this.startNode == null) { // Checks which draw tool is selected. Also limits only one start/end node
                    this.grid[gridY][gridX].setStart();
                    setStartNode(this.grid[gridY][gridX]);
                } else if (this.rbEndNode.isSelected() && this.endNode == null) {
                    this.grid[gridY][gridX].setEnd();
                    setEndNode(this.grid[gridY][gridX]);
                } else if (this.rbBarrierNode.isSelected()) {
                    this.grid[gridY][gridX].setBarrier();
                }
            } else if (SwingUtilities.isRightMouseButton(evt)) { // Right click (erase)
                if (this.grid[gridY][gridX].isStart()) { // Reset start/end node if it exists
                    setStartNode(null);
                } else if (this.grid[gridY][gridX].isEnd()) {
                    setEndNode(null);
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
        this.boardPanel.add(this.rbStartNode);
        this.rbStartNode.setBounds(20,100,200,20);
        this.rbStartNode.setFocusable(false);
        this.rbStartNode.addActionListener(this);

        this.boardPanel.add(this.rbEndNode);
        this.rbEndNode.setBounds(20,100+(20),200,20);
        this.rbEndNode.setFocusable(false);
        this.rbEndNode.addActionListener(this);

        this.boardPanel.add(this.rbBarrierNode);
        this.rbBarrierNode.setBounds(20,100+(20*2),100,20);
        this.rbBarrierNode.setFocusable(false);
        this.rbBarrierNode.addActionListener(this);

        this.boardPanel.add(this.labelErase);
        this.labelErase.setBounds(20,100+(20*3),150,20);

        this.boardPanel.add(this.butClearGrid);
        this.butClearGrid.setBounds(20,100+(20*5),150,40);
        this.butClearGrid.setFocusable(false);
        this.butClearGrid.addActionListener(this);

        ButtonGroup drawTools = new ButtonGroup(); // Only allows one draw tool radio button to be pressed at a time
        drawTools.add(this.rbStartNode);
        drawTools.add(this.rbEndNode);
        drawTools.add(this.rbBarrierNode);

        // Algorithm type radio buttons
        this.boardPanel.add(this.rbBFS);
        this.rbBFS.setBounds(20,300,200,20);
        this.rbBFS.setFocusable(false);
        this.rbBFS.addActionListener(this);

        this.boardPanel.add(this.rbDFS);
        this.rbDFS.setBounds(20,300+(20),200,20);
        this.rbDFS.setFocusable(false);
        this.rbDFS.addActionListener(this);

        this.boardPanel.add(this.rbAStar);
        this.rbAStar.setBounds(20,300+(20*2),200,20);
        this.rbAStar.setFocusable(false);
        this.rbAStar.addActionListener(this);

        this.boardPanel.add(this.rbDijkstra);
        this.rbDijkstra.setBounds(20,300+(20*3),200,20);
        this.rbDijkstra.setFocusable(false);
        this.rbDijkstra.addActionListener(this);

        this.boardPanel.add(this.butStartSearch);
        this.butStartSearch.setBounds(20,300+(20*5),150,40);
        this.butStartSearch.setFocusable(false);
        this.butStartSearch.addActionListener(this);
        this.butStartSearch.setEnabled(false); // Set to false until start/end nodes are created
        //this.butStartSearch.setBackground(Color.GREEN);

        ButtonGroup algoTypes = new ButtonGroup(); // Only allows one draw tool radio button to be pressed at a time
        algoTypes.add(this.rbBFS);
        algoTypes.add(this.rbDFS);
        algoTypes.add(this.rbAStar);
        algoTypes.add(this.rbDijkstra);

        // Generate node grid
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
