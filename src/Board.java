import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class Board extends MouseAdapter implements ActionListener, ChangeListener {
    // PROPERTIES
    private BoardPanel boardPanel = new BoardPanel(); // Create new boardPanel JPanel object

    // Grid
    public static Node[][] grid;
    public static int gridWidth = 25, nodeSideLength;
    private JSlider sliderGridWidth = new JSlider(10, 40, this.gridWidth); // Grid size slider
    private Node startNode, endNode;

    // Draw tools
    private JRadioButton rbStartNode = new JRadioButton("Start node", true);
    private JRadioButton rbEndNode = new JRadioButton("End node");
    private JRadioButton rbBarrierNode = new JRadioButton("Barrier node");
    private JLabel labelErase = new JLabel("Right click to erase nodes");
    private JButton butResetGrid = new JButton("Reset grid");
    private JButton butClearGrid = new JButton("Clear grid");
    private boolean disableDraw = false;

    // Algorithm types
    private JRadioButton rbBFS = new JRadioButton("Breadth-first search", true);
    private JRadioButton rbDFS = new JRadioButton("Depth-first search");
    private JRadioButton rbAStar = new JRadioButton("A* search algorithm");
    private JRadioButton rbDijkstra = new JRadioButton("Dijkstra's algorithm");

    private JButton butStartSearch = new JButton("Start pathfinding");
    private boolean disableAlgoSelect = false;
    private boolean pathfindingOngoing = false;
    private boolean pathfindingPaused = false;

    // Path node refresh slider
    private Thread threadPathNodeRefresh;
    private int refreshInterval = 15; // Used for connect path thread to adjust refresh interval for adding pathfinding nodes
    private JSlider sliderPathRefreshInterval = new JSlider(5, 35, refreshInterval);

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
        if (this.startNode == null) {
            this.rbStartNode.setText("Start node");
        } else {
            this.rbStartNode.setText("Start node: (" + (startNode.getX()+1) + ", " + (startNode.getY()+1) + ")"); // Shows where start node is located. (uses non-array x/y coordinates (+1 to each val))
        }
        updateStartSearchButton();
    }
    public void setEndNode(Node endNode) {
        this.endNode = endNode;
        if (endNode == null) {
            this.rbEndNode.setText("End node");
        } else {
            this.rbEndNode.setText("End node: (" + (endNode.getX()+1) + ", " + (endNode.getY()+1) + ")"); // Shows where end node is located. (uses non-array x/y coordinates (+1 to each val))
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

    public void generateNewGrid(int width) { // Generate a square grid
        resetCurrentGrid(); // Resets boardPanel variables if grid already exists
        this.grid = new Node[width][width];
        for (int y=0; y < width; y++) { // Load Node grid/array
            for (int x=0; x < width; x++) {
                this.grid[y][x] = new Node(x,y);
            }
        }

        this.gridWidth = width;
        this.nodeSideLength = Main.FRAME_HEIGHT/Board.grid.length;

        setStartNode(null); // Reset start/end nodes
        setEndNode(null);
    }
    public void resetCurrentGrid() { // Clear pathfinding nodes (exclude start, end, & barrier nodes)
        if (this.grid != null) {
            for (int y = 0; y < this.grid.length; y++) { // Load Node grid/array
                for (int x = 0; x < this.grid[y].length; x++) {
                    if (this.grid[y][x].isStart()) {
                        this.grid[y][x] = new Node(x,y, Node.START_NODE);
                    } else if (this.grid[y][x].isEnd()) {
                        this.grid[y][x] = new Node(x,y, Node.END_NODE);
                    } else if (this.grid[y][x].isBarrier()) {
                        this.grid[y][x] = new Node(x,y, Node.BARRIER_NODE);
                    } else {
                        this.grid[y][x] = new Node(x, y);
                    }
                }
            }
        }
        this.boardPanel.endNotFound(false);
        this.boardPanel.searchComplete(false);
    }

    public void connectPath(ArrayList<Node> path) { // Add path to grid
        this.threadPathNodeRefresh = new Thread(() -> {
            for (Node pathNode : path) { // Loop through path to add to grid
                if (!pathNode.isStart() && !pathNode.isEnd()) { // Ignore start/end node to prevent drawing over
                    pathNode.setSearched(false);
                    try {
                        Thread.sleep(this.refreshInterval); // Delay before next path node is added to grid
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                    while (this.pathfindingPaused == true) { // Loop to pause pathfinding TODO: remove sout since empty while loop doesn't run
                        System.out.println("Pausing");
                    }
                }
            }

            if (!path.contains(endNode)) {
                try {
                    Thread.sleep(this.refreshInterval); // Delay
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
                boardPanel.endNotFound(true);
            }

            resetPathfinding(); // Reset variables after pathfinding complete
            disableAlgorithmSelect(false);
            this.boardPanel.searchComplete(true);
        });
        this.threadPathNodeRefresh.start();
    }
    public void connectPath(ArrayList<Node> searchPath, ArrayList<Node> shortestPath) { // Add multiple paths to grid
        this.threadPathNodeRefresh = new Thread(() -> {
            if (searchPath != null) {
                for (Node pathNode : searchPath) { // Loop through path to add to grid
                    if (!pathNode.isStart() && !pathNode.isEnd()) { // Ignore start/end node to prevent drawing over
                        pathNode.setSearched(false);
                        try {
                            Thread.sleep(this.refreshInterval); // Delay before next path node is added to grid
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        while (this.pathfindingPaused == true) { // Loop to pause pathfinding TODO: remove sout since empty while loop doesn't run
                            System.out.println("Pausing");
                        }
                    }
                }
            }

            if (shortestPath != null) {
                for (Node pathNode : shortestPath) { // Loop through path to add to grid
                    if (!pathNode.isStart() && !pathNode.isEnd()) { // Ignore start/end node to prevent drawing over
                        pathNode.setSearched(true);
                        try {
                            Thread.sleep(this.refreshInterval); // Delay before next path node is added to grid
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        while (this.pathfindingPaused == true) { // Loop to pause pathfinding TODO: remove sout since empty while loop doesn't run
                            System.out.println("Pausing");
                        }
                    }
                }
            }

            resetPathfinding(); // Reset variables after pathfinding complete
            disableAlgorithmSelect(false);
            this.boardPanel.searchComplete(true);
        });
        this.threadPathNodeRefresh.start();
    }
    /*public void connectMultiplePaths(ArrayList<ArrayList<Node>> listPaths) { // Add multiple paths to grid
        this.threadPathNodeRefresh = new Thread(() -> {
            for (ArrayList<Node> path : listPaths) {
                for (Node pathNode : path) { // Loop through path to add to grid
                    if (!pathNode.isStart() && !pathNode.isEnd()) { // Ignore start/end node to prevent drawing over
                        pathNode.setSearched();
                        try {
                            Thread.sleep(this.refreshInterval); // Delay before next path node is added to grid
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        while (this.pathfindingPaused == true) { // Loop to pause pathfinding TODO: remove sout since empty while loop doesn't run
                            System.out.println("Pausing");
                        }
                    }
                }
                if (!path.contains(endNode)) {
                    try {
                        Thread.sleep(this.refreshInterval); // Delay
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                    boardPanel.endNotFound(true);
                }
            }

            resetPathfinding(); // Reset variables after pathfinding complete
            disableAlgorithmSelect(false);
        });
        this.threadPathNodeRefresh.start();
    }*/

    // LISTENER METHODS
    // ActionListener
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == this.timerBoard) { // 60FPS Timer
            this.boardPanel.repaint();
        } else if (evt.getSource() == this.butResetGrid) {
            resetCurrentGrid(); // Clear pathfinding nodes
            disableDrawing(false); // Enable drawing
            disableAlgorithmSelect(false); // Enable algorithm selection

            resetPathfinding(); // Reset variables after pathfinding complete
        } else if (evt.getSource() == this.butClearGrid) {
            if (JOptionPane.showConfirmDialog(null, "Are you sure you want to clear the grid?", "Warning",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { // Confirm clear grid dialog. Yes option.
                generateNewGrid(gridWidth); // Regenerate grid
                disableDrawing(false); // Enable drawing
                disableAlgorithmSelect(false); // Enable algorithm selection

                resetPathfinding(); // Reset variables after pathfinding complete
            } else { // No option
            }
        } else if (evt.getSource() == this.butStartSearch) { // Start pathfinding TODO: finish methods
            disableDrawing(true); // Prevent drawing during pathfinding
            disableAlgorithmSelect(true); // Prevent changing algorithm during pathfinding

            if (this.pathfindingOngoing == true) { // Pathfinding is ongoing
                if (this.pathfindingPaused == true) { // Pathfinding is currently paused and will be resumed
                    this.butStartSearch.setText("Pause pathfinding");
                    this.pathfindingPaused = false;
                } else { // Pathfinding is currently ongoing and will be paused
                    this.butStartSearch.setText("Resume pathfinding");
                    this.pathfindingPaused = true;
                }
            } else { // Not searching yet
                resetCurrentGrid();
                this.pathfindingOngoing = true;
                this.pathfindingPaused = false;
                this.butStartSearch.setText("Pause pathfinding");

                Pathfinder pathfinder = new Pathfinder(this.grid, this.startNode, this.endNode);
                ArrayList<Node> searchPath = new ArrayList<>(), shortestPath;

                if (this.rbBFS.isSelected()) { // Breadth-first search
                    searchPath = pathfinder.bfs();
                } else if (this.rbDFS.isSelected()) { // Depth-first search
                    searchPath = pathfinder.dfs();
                } else if (this.rbAStar.isSelected()) { // A* search algorithm
                    searchPath = pathfinder.astar();
                } else if (this.rbDijkstra.isSelected()) { // Dijkstra's algorithm
                    searchPath = pathfinder.dijkstra();
                }

                shortestPath = pathfinder.getShortestPath();
                if (shortestPath != null) { // Reached end node
                    //connectMultiplePaths(new ArrayList<>(Arrays.asList(searchPath, shortestPath)));
                    connectPath(searchPath, shortestPath);
                } else { // Didn't reach end node
                    connectPath(searchPath);
                }
            }
        }
    }
    public void resetPathfinding() { // Reset pathfinding variables
        this.pathfindingOngoing = false;
        this.pathfindingPaused = false;
        this.butStartSearch.setText("Start pathfinding");
    }

    // ChangeListener
    public void stateChanged(ChangeEvent evt) {
        if (evt.getSource() == this.sliderGridWidth) {
            this.gridWidth = this.sliderGridWidth.getValue();
            generateNewGrid(this.gridWidth);
            System.out.println("Grid width: " + this.gridWidth);
        } else if (evt.getSource() == this.sliderPathRefreshInterval) {
            this.refreshInterval = this.sliderPathRefreshInterval.getValue();
        }
    }

    // MouseAdapter (MouseListener/MouseMotionListener)
    public void mousePressed(MouseEvent evt) {
        mouseDrawing(evt);
    }
    public void mouseDragged(MouseEvent evt) {
        mouseDrawing(evt);
    }
    public void mouseDrawing(MouseEvent evt) { // Method for draw tools (aggregates mousePressed & mouseDragged)
        int mouseX = evt.getX(), mouseY = evt.getY();
        if (mouseX >= Main.MENU_WIDTH && mouseX < Main.FRAME_WIDTH && mouseY >= 0 && mouseY < Main.FRAME_HEIGHT && this.grid != null) { // Mouse pointer is within grid (not in menu bar)
            int gridX = (mouseX-Main.MENU_WIDTH) / this.nodeSideLength;
            int gridY = mouseY / this.nodeSideLength;

            if (this.disableDraw == false) { // Drawing is enabled
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
    }
    public void disableAlgorithmSelect(boolean disableAlgoSelect) {
        this.disableAlgoSelect = disableAlgoSelect;
        if (this.disableAlgoSelect == true) { // Algorithm select has been disabled; prevent changes to pathfinding radio buttons
            this.rbBFS.setEnabled(false);
            this.rbDFS.setEnabled(false);
            this.rbAStar.setEnabled(false);
            this.rbDijkstra.setEnabled(false);
        } else { // Algorithm select has been enabled; allow changes to pathfinding radio buttons
            this.rbBFS.setEnabled(true);
            this.rbDFS.setEnabled(true);
            this.rbAStar.setEnabled(true);
            this.rbDijkstra.setEnabled(true);
        }
    }
    public void disableDrawing(boolean disableDraw) {
        this.disableDraw = disableDraw;
        if (this.disableDraw == true) { // Drawing has been disabled; prevent changes to draw tool radio buttons
            this.rbStartNode.setEnabled(false);
            this.rbEndNode.setEnabled(false);
            this.rbBarrierNode.setEnabled(false);

            this.sliderGridWidth.setEnabled(false);
        } else { // Drawing has been enabled; allow changes to draw tool radio buttons
            this.rbStartNode.setEnabled(true);
            this.rbEndNode.setEnabled(true);
            this.rbBarrierNode.setEnabled(true);

            this.sliderGridWidth.setEnabled(true);
        }
    }

    // CONSTRUCTOR
    public Board() {
        this.boardPanel.setPreferredSize(new Dimension(Main.FRAME_WIDTH, Main.FRAME_HEIGHT));
        this.boardPanel.setLayout(null);
        this.boardPanel.addMouseListener(this);
        this.boardPanel.addMouseMotionListener(this);
        //this.boardPanel.setBackground(Color.DARK_GRAY);

        // DRAW TOOL RADIO BUTTONS
        ButtonGroup drawTools = new ButtonGroup(); // Only allows one draw tool radio button to be pressed at a time
        drawTools.add(this.rbStartNode);
        drawTools.add(this.rbEndNode);
        drawTools.add(this.rbBarrierNode);

        // Start node
        this.boardPanel.add(this.rbStartNode);
        this.rbStartNode.setBounds(20,100,200,20);
        this.rbStartNode.setFocusable(false);
        this.rbStartNode.addActionListener(this);

        // End node
        this.boardPanel.add(this.rbEndNode);
        this.rbEndNode.setBounds(20,100+(20),200,20);
        this.rbEndNode.setFocusable(false);
        this.rbEndNode.addActionListener(this);

        // Barrier node
        this.boardPanel.add(this.rbBarrierNode);
        this.rbBarrierNode.setBounds(20,100+(20*2),100,20);
        this.rbBarrierNode.setFocusable(false);
        this.rbBarrierNode.addActionListener(this);

        this.boardPanel.add(this.labelErase); // "Right click to erase" label
        this.labelErase.setBounds(20,100+(20*3),150,20);

        // Grid width slider
        this.boardPanel.add(this.sliderGridWidth);
        this.sliderGridWidth.setBounds(20,100+(20*4),150,50);
        this.sliderGridWidth.setPaintLabels(true);
        this.sliderGridWidth.setPaintTicks(true);
        this.sliderGridWidth.setMajorTickSpacing(10);
        this.sliderGridWidth.setMinorTickSpacing(5);
        this.sliderGridWidth.setFocusable(false);
        this.sliderGridWidth.addChangeListener(this);
        generateNewGrid(this.sliderGridWidth.getValue()); // Generate node grid

        // Reset grid button
        this.boardPanel.add(this.butResetGrid);
        this.butResetGrid.setBounds(20,100+(20*7),150,40);
        this.butResetGrid.setFocusable(false);
        this.butResetGrid.addActionListener(this);

        // Clear grid button
        this.boardPanel.add(this.butClearGrid);
        this.butClearGrid.setBounds(20,100+(20*9),150,40);
        this.butClearGrid.setFocusable(false);
        this.butClearGrid.addActionListener(this);

        // ALGORITHM TYPE RADIO BUTTONS
        ButtonGroup algoTypes = new ButtonGroup(); // Only allows one draw tool radio button to be pressed at a time
        algoTypes.add(this.rbBFS);
        algoTypes.add(this.rbDFS);
        algoTypes.add(this.rbAStar);
        algoTypes.add(this.rbDijkstra);

        // BFS
        this.boardPanel.add(this.rbBFS);
        this.rbBFS.setBounds(20,350,200,20);
        this.rbBFS.setFocusable(false);
        this.rbBFS.addActionListener(this);

        // DFS
        this.boardPanel.add(this.rbDFS);
        this.rbDFS.setBounds(20,350+(20),200,20);
        this.rbDFS.setFocusable(false);
        this.rbDFS.addActionListener(this);

        // A*
        this.boardPanel.add(this.rbAStar);
        this.rbAStar.setBounds(20,350+(20*2),200,20);
        this.rbAStar.setFocusable(false);
        this.rbAStar.addActionListener(this);

        // Dijkstra
        this.boardPanel.add(this.rbDijkstra);
        this.rbDijkstra.setBounds(20,350+(20*3),200,20);
        this.rbDijkstra.setFocusable(false);
        this.rbDijkstra.addActionListener(this);

        // Path node refresh slider
        this.boardPanel.add(this.sliderPathRefreshInterval);
        this.sliderPathRefreshInterval.setBounds(20,350+(20*4),150,50);
        this.sliderPathRefreshInterval.setPaintLabels(true);
        this.sliderPathRefreshInterval.setPaintTicks(true);
        this.sliderPathRefreshInterval.setMajorTickSpacing(15);
        this.sliderPathRefreshInterval.setMinorTickSpacing(5);
        this.sliderPathRefreshInterval.setSnapToTicks(true);
        this.sliderPathRefreshInterval.setInverted(true);
        this.sliderPathRefreshInterval.setFocusable(false);
        this.sliderPathRefreshInterval.addChangeListener(this);

        Hashtable<Integer, JLabel> tablePathRefreshInterval = new Hashtable<>();
        tablePathRefreshInterval.put(this.sliderPathRefreshInterval.getMinimum(), new JLabel("Fast"));
        tablePathRefreshInterval.put((this.sliderPathRefreshInterval.getMaximum()+this.sliderPathRefreshInterval.getMinimum())/2, new JLabel("Medium"));
        tablePathRefreshInterval.put(this.sliderPathRefreshInterval.getMaximum(), new JLabel("Slow"));
        this.sliderPathRefreshInterval.setLabelTable(tablePathRefreshInterval);

        // Start pathfinding button (also resume/pause)
        this.boardPanel.add(this.butStartSearch);
        this.butStartSearch.setBounds(20,350+(20*7),150,40);
        this.butStartSearch.setFocusable(false);
        this.butStartSearch.addActionListener(this);
        this.butStartSearch.setEnabled(false); // Set to false until start/end nodes are created
        //this.butStartSearch.setBackground(Color.GREEN);

        this.timerBoard.start(); // 60FPS repaint timer
    }
} class BoardPanel extends JPanel {
    private boolean endNotFound = false;
    private boolean pathfindingComplete = false;
    private int r=255,g=0,b=0;

    public void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D)graphics;
        super.paintComponent(g2);

        if (pathfindingComplete == true) { // Modified RGB Color fade algorithm from https://codepen.io/Codepixl/pen/ogWWaK/
            int increment = 5;
            for (int i=0; i<increment; i++) { // TODO: optimize algorithm to not use for loop. also limit range from 15-240?
                if (r > 0 && b == 0) {
                    r--;
                    g++;
                }
                if (g > 0 && r == 0) {
                    g--;
                    b++;
                }
                if (b > 0 && g == 0) {
                    r++;
                    b--;
                }
            }
        }

        drawNodes(g2, Board.grid, Board.nodeSideLength);
        drawGrid(g2, Board.grid, Board.nodeSideLength);

        if (this.endNotFound == true) {
            g2.setColor(new Color(0,0,0,0.5f)); // Darken screen (Black w/ 50% opacity)
            g2.fillRect(Main.MENU_WIDTH,0, Main.FRAME_HEIGHT, Main.FRAME_HEIGHT);

            g2.setColor(Color.ORANGE);
            g2.setFont(Main.loadFont("product_sans_bold",70));
            String noPathFound = "NO PATH";
            int stringWidth = g2.getFontMetrics().stringWidth(noPathFound);
            g2.drawString(noPathFound, Main.MENU_WIDTH+(Main.FRAME_HEIGHT/2)-(stringWidth/2), (Main.FRAME_HEIGHT/2));
        }

    }

    public void drawGrid(Graphics2D g2, Node[][] grid, int sideLength) {
        for (int i=0; i<=grid.length; i++) { // Horizontal grid lines
            g2.drawLine(Main.MENU_WIDTH,i*sideLength, Main.FRAME_WIDTH,i*sideLength);
        }
        for (int j=0; j<=grid[0].length; j++) { // Vertical grid lines
           g2.drawLine(Main.MENU_WIDTH+(j*sideLength), 0, Main.MENU_WIDTH+(j*sideLength), Main.FRAME_HEIGHT);
        }
    }
    public void drawNodes(Graphics2D g2, Node[][] grid, int sideLength) {
        for (int i=0; i<grid.length; i++) {
            for (int j=0; j<grid[i].length; j++) {
                g2.setColor(grid[i][j].getColor());
                if (this.pathfindingComplete && grid[i][j].isEmpty()) { // Color shift white empty nodes after pathfinding is done
                    g2.setColor(new Color(r, g, b));
                }
                g2.fillRect(Main.MENU_WIDTH+(j*sideLength), i*sideLength, sideLength, sideLength); // Draw node

                if (grid[i][j].isStart() || grid[i][j].isEnd()) { // Draw border around start/end nodes
                    g2.setStroke(new BasicStroke(2));
                    g2.setColor(Color.BLACK);
                    g2.drawRect(Main.MENU_WIDTH+(j*sideLength)+1, (i*sideLength)+1, sideLength-1, sideLength-1); // Draw node
                    g2.setStroke(new BasicStroke(1));
                }
            }
        }
        g2.setColor(Color.BLACK);
    }
    public void endNotFound(boolean endNodeNotFound) {
        this.endNotFound = endNodeNotFound;
    }
    public void searchComplete(boolean pathfindingComplete) {
        this.pathfindingComplete = pathfindingComplete;
    }
}
