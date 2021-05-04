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
    private JLabel labelGridWidth = new JLabel("Grid Width:");
    private JSlider sliderGridWidth = new JSlider(10, 40, this.gridWidth); // Grid size slider
    private Node startNode, endNode;

    // Menu title/author labels
    private JLabel labelMenuTitle = new JLabel("Pathfinding Visualizer");
    private JLabel labelMenuAuthor = new JLabel("By Henry Wong");

    // Drawing tools
    private JLabel labelDrawingTools = new JLabel("Drawing Tools:");
    private JRadioButton rbStartNode = new JRadioButton("Start node", true);
    private JRadioButton rbEndNode = new JRadioButton("End node");
    private JRadioButton rbBarrierNode = new JRadioButton("Barrier node");
    private JLabel labelErase = new JLabel("Right click to erase nodes");
    private JButton butResetGrid = new JButton("Reset grid");
    private JButton butClearGrid = new JButton("Clear grid");
    private boolean disableDraw = false;

    // Pathfinding algorithm types
    private JLabel labelPathfindingAlgos = new JLabel("Pathfinding Algorithms:");
    private final String COMBOBOX_ASTAR = "A* Search Algorithm", // ComboBox strings used for indexing pathfinding algorithms
            COMBOBOX_DIJKSTRA = "Dijkstra's Algorithm",
            COMBOBOX_BFS = "Breadth-First Search",
            COMBOBOX_DFS = "Depth-First Search";
    private JComboBox pathfindingAlgosComboBox = new JComboBox(new String[] { // Dropdown menu of pathfinding algorithms (TODO: Add Dijkstra)
            COMBOBOX_ASTAR, /*COMBOBOX_DIJKSTRA,*/ COMBOBOX_BFS, COMBOBOX_DFS
    });

    private JButton butStartSearch = new JButton("START PATHFINDING");
    private boolean disableAlgoSelect = false;
    private boolean pathfindingOngoing = false;
    private boolean pathfindingPaused = false;

    // Maze generator algorithm types
    private JLabel labelMazeGeneratorAlgos = new JLabel("Maze Generation:");
    private final String COMBOBOX_NONE = "None", // ComboBox strings used for indexing maze generator algorithms
            COMBOBOX_RECURSIVE_DIVISION = "Recursive Division";
    private JComboBox mazeGeneratorAlgosComboBox = new JComboBox(new String[] { // Dropdown menu of maze generator algorithms
            COMBOBOX_NONE, COMBOBOX_RECURSIVE_DIVISION
    });
    private JButton butGenerateMaze = new JButton("Generate");

    // Path node refresh slider
    private Thread threadPathNodeRefresh;
    private int refreshInterval = 15; // Used for connect path thread to adjust refresh interval for adding pathfinding nodes
    private JLabel labelPathRefreshInterval = new JLabel("Visualizer Speed:");
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
    public void clearGrid() { // Completely clear grid
        generateNewGrid(gridWidth); // Regenerate grid
        disableDrawing(false); // Enable drawing
        disableAlgorithmSelect(false); // Enable algorithm selection

        resetPathfinding(); // Reset variables after pathfinding complete
    }

    public void connectPath(ArrayList<Node> path) { // Add path to grid
        this.threadPathNodeRefresh = new Thread(() -> {
            for (Node pathNode : path) { // Loop through path to add to grid
                if (!pathNode.isStart() && !pathNode.isEnd()) { // Ignore start/end node to prevent drawing over
                    try {
                        Thread.sleep(this.refreshInterval); // Delay before next path node is added to grid
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                    pathNode.setSearched(false);
                    while (this.pathfindingPaused == true) { // Loop to pause pathfinding TODO: remove sout (empty while loop doesn't run)
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
        });
        this.threadPathNodeRefresh.start();
    }
    public void connectPath(ArrayList<Node> searchPath, ArrayList<Node> shortestPath) { // Add multiple paths to grid
        this.threadPathNodeRefresh = new Thread(() -> {
            if (searchPath != null) {
                for (Node pathNode : searchPath) { // Loop through path to add to grid
                    if (!pathNode.isStart() && !pathNode.isEnd()) { // Ignore start/end node to prevent drawing over
                        try {
                            Thread.sleep(this.refreshInterval); // Delay before next path node is added to grid
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        pathNode.setSearched(false);
                        while (this.pathfindingPaused == true) { // Loop to pause pathfinding TODO: remove sout (empty while loop doesn't run)
                            System.out.println("Pausing");
                        }
                    }
                }
            }

            if (shortestPath != null) {
                for (Node pathNode : shortestPath) { // Loop through path to add to grid
                    if (!pathNode.isStart() && !pathNode.isEnd()) { // Ignore start/end node to prevent drawing over
                        try {
                            Thread.sleep(this.refreshInterval); // Delay before next path node is added to grid
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        pathNode.setSearched(true);
                        while (this.pathfindingPaused == true) { // Loop to pause pathfinding TODO: remove sout since empty while loop doesn't run
                            System.out.println("Pausing");
                        }
                    }
                }
            }
        });
        this.threadPathNodeRefresh.start();
    }

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
                clearGrid();
            }
        } else if (evt.getSource() == this.butStartSearch) { // Start pathfinding
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

                switch ((String) this.pathfindingAlgosComboBox.getSelectedItem()) { // Get selected pathfinding algorithm
                    case COMBOBOX_ASTAR: // A* search algorithm
                        searchPath = pathfinder.astar();
                        break;
                    case COMBOBOX_DIJKSTRA: // Dijkstra's algorithm
                        searchPath = pathfinder.dijkstra();
                        break;
                    case COMBOBOX_BFS: // Breadth-first search
                        searchPath = pathfinder.bfs();
                        break;
                    case COMBOBOX_DFS: // Depth-first search
                        searchPath = pathfinder.dfs();
                        break;
                }

                shortestPath = pathfinder.getShortestPath();
                if (shortestPath != null) { // Reached end node
                    //connectMultiplePaths(new ArrayList<>(Arrays.asList(searchPath, shortestPath)));
                    connectPath(searchPath, shortestPath);
                } else { // Didn't reach end node
                    connectPath(searchPath);
                }

                new Thread(() -> { // Thread to wait for connect path thread to end
                    if (this.threadPathNodeRefresh != null) { // If connectPath thread is running, wait for it to die before executing
                        try {
                            this.threadPathNodeRefresh.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        resetPathfinding(); // Reset variables after pathfinding complete
                        disableAlgorithmSelect(false); // Enable algorithm select radio buttons
                        this.boardPanel.searchComplete(true); // Enable colour fade animation of empty nodes
                    }
                }).start();
            }
        } else if (evt.getSource() == this.mazeGeneratorAlgosComboBox) {
            if (this.mazeGeneratorAlgosComboBox.getSelectedItem() == COMBOBOX_NONE) {
                this.butGenerateMaze.setEnabled(false);
            } else {
                this.butGenerateMaze.setEnabled(true);
            }
        } else if (evt.getSource() == this.butGenerateMaze) {
            new Thread(() -> {
                if (JOptionPane.showConfirmDialog(null, "To generate a maze the current grid will be reset. Are you sure you want to clear the grid?", "Warning",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { // Confirm clear grid dialog. Yes option.
                    clearGrid();

                    switch ((String) this.mazeGeneratorAlgosComboBox.getSelectedItem()) { // Get selected pathfinding algorithm
                        case COMBOBOX_NONE: // Empty
                            break;
                        case COMBOBOX_RECURSIVE_DIVISION: // Recursive division maze generation
                            MazeGenerator.recursiveDivision(this.grid, 0, 0, gridWidth-1, gridWidth-1);
                            System.out.println("RD Done");
                            break;
                    }
                }
            }).start();
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
            this.pathfindingAlgosComboBox.setEnabled(false);
        } else { // Algorithm select has been enabled; allow changes to pathfinding radio buttons
            this.pathfindingAlgosComboBox.setEnabled(true);
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

        // MENU TITLE/AUTHOR (Top left)
        this.boardPanel.add(labelMenuTitle);
        this.labelMenuTitle.setFont(Main.FONT_TITLE);
        this.labelMenuTitle.setBounds(Main.MENU_PADDING, Main.MENU_PADDING+10, Main.MENU_WIDTH-(Main.MENU_PADDING*2), 30);

        this.boardPanel.add(labelMenuAuthor);
        this.labelMenuAuthor.setFont(Main.FONT_SUBHEADER);
        this.labelMenuAuthor.setBounds(Main.MENU_PADDING,Main.MENU_PADDING+35, Main.MENU_WIDTH-(Main.MENU_PADDING*2), 25);

        // DRAW TOOL RADIO BUTTONS
        ButtonGroup drawTools = new ButtonGroup(); // Only allows one draw tool radio button to be pressed at a time
        drawTools.add(this.rbStartNode);
        drawTools.add(this.rbEndNode);
        drawTools.add(this.rbBarrierNode);

        this.boardPanel.add(this.labelDrawingTools);
        this.labelDrawingTools.setFont(Main.FONT_HEADER);
        this.labelDrawingTools.setBounds(Main.MENU_PADDING,Main.MENU_PADDING+80, Main.MENU_WIDTH-(Main.MENU_PADDING*2), 30);

        // Start node
        this.boardPanel.add(this.rbStartNode);
        this.rbStartNode.setBounds(Main.MENU_PADDING,Main.MENU_PADDING+90+20,200,20);
        this.rbStartNode.setFocusable(false);
        this.rbStartNode.addActionListener(this);

        // End node
        this.boardPanel.add(this.rbEndNode);
        this.rbEndNode.setBounds(Main.MENU_PADDING,Main.MENU_PADDING+90+(20*2),200,20);
        this.rbEndNode.setFocusable(false);
        this.rbEndNode.addActionListener(this);

        // Barrier node
        this.boardPanel.add(this.rbBarrierNode);
        this.rbBarrierNode.setBounds(Main.MENU_PADDING,Main.MENU_PADDING+90+(20*3),200,20);
        this.rbBarrierNode.setFocusable(false);
        this.rbBarrierNode.addActionListener(this);

        this.boardPanel.add(this.labelErase); // "Right click to erase" label
        this.labelErase.setFont(Main.FONT_NORMAL_ITALICS);
        this.labelErase.setBounds(Main.MENU_PADDING,Main.MENU_PADDING+90+(20*4),150,20);

        // Reset grid button
        this.boardPanel.add(this.butResetGrid);
        this.butResetGrid.setBounds(Main.MENU_PADDING,Main.MENU_PADDING+90+10+(20*5), (Main.MENU_WIDTH/2)-Main.MENU_PADDING,40);
        this.butResetGrid.setFocusable(false);
        this.butResetGrid.addActionListener(this);

        // Clear grid button
        this.boardPanel.add(this.butClearGrid);
        this.butClearGrid.setBounds(this.butResetGrid.getWidth()+Main.MENU_PADDING,Main.MENU_PADDING+90+10+(20*5),(Main.MENU_WIDTH/2)-Main.MENU_PADDING,40);
        this.butClearGrid.setFocusable(false);
        this.butClearGrid.addActionListener(this);

        // GRID WIDTH SLIDER
        // "Grid Width" JLabel/Header
        this.boardPanel.add(this.labelGridWidth);
        this.labelGridWidth.setFont(Main.FONT_HEADER);
        this.labelGridWidth.setBounds(Main.MENU_PADDING,Main.MENU_PADDING+90+(20*8), Main.MENU_WIDTH-(Main.MENU_PADDING*2), 30);

        // Grid width slider
        this.boardPanel.add(this.sliderGridWidth);
        this.sliderGridWidth.setBounds(Main.MENU_PADDING,Main.MENU_PADDING+90+5+(20*9), Main.MENU_WIDTH-(Main.MENU_PADDING*2),50);
        this.sliderGridWidth.setPaintLabels(true);
        this.sliderGridWidth.setPaintTicks(true);
        this.sliderGridWidth.setMajorTickSpacing(10);
        this.sliderGridWidth.setMinorTickSpacing(5);
        this.sliderGridWidth.setFocusable(false);
        this.sliderGridWidth.addChangeListener(this);
        generateNewGrid(this.sliderGridWidth.getValue()); // Generate node grid

        // PATHFINDING ALGORITHM TYPE DROPDOWN MENU (ComboBox)
        // "Pathfinding Algorithms" JLabel/Header
        this.boardPanel.add(this.labelPathfindingAlgos);
        this.labelPathfindingAlgos.setFont(Main.FONT_HEADER);
        this.labelPathfindingAlgos.setBounds(Main.MENU_PADDING,350, Main.MENU_WIDTH-(Main.MENU_PADDING*2), 30);

        // Pathfinding Algorithms Dropdown (ComboBox)
        this.boardPanel.add(this.pathfindingAlgosComboBox);
        this.pathfindingAlgosComboBox.setBounds(Main.MENU_PADDING, 350+30, Main.MENU_WIDTH-(Main.MENU_PADDING*2),30);
        this.pathfindingAlgosComboBox.setFocusable(false);

        /*
        // MAZE GENERATOR ALGORITHM TYPE DROPDOWN MENU (ComboBox)
        // "Maze Generation" JLabel/Header
        this.boardPanel.add(this.labelMazeGeneratorAlgos);
        this.labelMazeGeneratorAlgos.setFont(Main.FONT_HEADER);
        this.labelMazeGeneratorAlgos.setBounds(Main.MENU_PADDING,420, Main.MENU_WIDTH-(Main.MENU_PADDING*2), 30);

        // Maze Generator Algorithms Dropdown (ComboBox)
        this.boardPanel.add(this.mazeGeneratorAlgosComboBox);
        this.mazeGeneratorAlgosComboBox.setBounds(Main.MENU_PADDING, 420+30, Main.MENU_WIDTH-(Main.MENU_PADDING*2)-80-5,30);
        this.mazeGeneratorAlgosComboBox.setFocusable(false);
        this.mazeGeneratorAlgosComboBox.addActionListener(this);

        // Maze Generator Button
        this.boardPanel.add(this.butGenerateMaze);
        this.butGenerateMaze.setBounds(Main.MENU_WIDTH-(Main.MENU_PADDING)-80,420+30-1,80,32);
        this.butGenerateMaze.setFocusable(false);
        this.butGenerateMaze.addActionListener(this);
        this.butGenerateMaze.setEnabled(false); // When maze generator algo is none, set to off. (Default to off)
        */

        // PATH NODE REFRESH/VISUALIZER SLIDER
        // "Visualizer Speed" JLabel/Header
        this.boardPanel.add(this.labelPathRefreshInterval);
        this.labelPathRefreshInterval.setFont(Main.FONT_HEADER);
        this.labelPathRefreshInterval.setBounds(Main.MENU_PADDING,490, Main.MENU_WIDTH-(Main.MENU_PADDING*2), 30);

        // Pathnode refresh slider
        this.boardPanel.add(this.sliderPathRefreshInterval);
        this.sliderPathRefreshInterval.setBounds(Main.MENU_PADDING,490+25,Main.MENU_WIDTH-(Main.MENU_PADDING*2),50);
        this.sliderPathRefreshInterval.setPaintLabels(true);
        this.sliderPathRefreshInterval.setPaintTicks(true);
        this.sliderPathRefreshInterval.setMajorTickSpacing(15);
        this.sliderPathRefreshInterval.setMinorTickSpacing(5);
        //this.sliderPathRefreshInterval.setSnapToTicks(true);
        this.sliderPathRefreshInterval.setInverted(true);
        this.sliderPathRefreshInterval.setFocusable(false);
        this.sliderPathRefreshInterval.addChangeListener(this);

        // Pathnode refresh slider intervals
        Hashtable<Integer, JLabel> tablePathRefreshInterval = new Hashtable<>();
        tablePathRefreshInterval.put(this.sliderPathRefreshInterval.getMinimum(), new JLabel("Fast"));
        tablePathRefreshInterval.put((this.sliderPathRefreshInterval.getMaximum()+this.sliderPathRefreshInterval.getMinimum())/2, new JLabel("Medium"));
        tablePathRefreshInterval.put(this.sliderPathRefreshInterval.getMaximum(), new JLabel("Slow"));
        this.sliderPathRefreshInterval.setLabelTable(tablePathRefreshInterval);

        // START PATHFINDING BUTTON (also resume/pause)
        this.boardPanel.add(this.butStartSearch);
        this.butStartSearch.setFont(Main.loadFont("Roboto-Bold", 18));
        this.butStartSearch.setBounds(Main.MENU_PADDING,650,Main.MENU_WIDTH-(Main.MENU_PADDING*2),70);
        Main.centerJButton(this.butStartSearch, Main.MENU_WIDTH);
        this.butStartSearch.setFocusable(false);
        this.butStartSearch.addActionListener(this);
        this.butStartSearch.setEnabled(false); // Set to false until start/end nodes are created
        this.butStartSearch.setBackground(Color.GREEN);

        this.timerBoard.start(); // 60FPS repaint timer
    }
} class BoardPanel extends JPanel {
    private boolean endNotFound = false;
    private boolean pathfindingComplete = false;
    private int rgbMax = 230, rgbMin = 15; // Limit color fade range
    private int r=rgbMax, g=rgbMin, b=rgbMin;

    public void paintComponent(Graphics graphic) {
        Graphics2D g2 = (Graphics2D)graphic;
        super.paintComponent(g2);

        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0,0, Main.MENU_WIDTH, (Main.MENU_PADDING*2)+70);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, (Main.MENU_PADDING*2)+70, Main.MENU_WIDTH, (Main.MENU_PADDING*2)+70);
        g2.drawLine(0, 340, Main.MENU_WIDTH, 340);
        g2.setStroke(new BasicStroke(1));

        if (pathfindingComplete == true) { // Modified RGB Color fade algorithm from https://codepen.io/Codepixl/pen/ogWWaK/
            /*r = r<0 ? 0 : Math.min(r, this.rgbMax); // Out of bound checks
            g = g<0 ? 0 : Math.min(g, this.rgbMax);
            b = b<0 ? 0 : Math.min(b, this.rgbMax);*/
            int increment = 2;
            if (this.r>this.rgbMin && this.g<this.rgbMax && this.b<=this.rgbMin) {
                this.r -= increment;
                this.g += increment;
            }
            if (this.g>this.rgbMin && this.b<this.rgbMax && this.r<=this.rgbMin) {
                this.g -= increment;
                this.b += increment;
            }
            if (this.b>this.rgbMin && this.r<this.rgbMax && this.g<=this.rgbMin) {
                this.r += increment;
                this.b -= increment;
            }
        }

        drawNodes(g2, Board.grid, Board.nodeSideLength);
        drawGridLines(g2, Board.grid, Board.nodeSideLength);

        if (this.endNotFound == true) {
            g2.setColor(new Color(0,0,0,0.5f)); // Darken screen (Black w/ 50% opacity)
            g2.fillRect(Main.MENU_WIDTH,0, Main.FRAME_HEIGHT, Main.FRAME_HEIGHT);

            g2.setColor(Color.ORANGE);
            g2.setFont(Main.loadFont("Roboto-Bold",70));
            String noPathFound = "NO PATH";
            int stringWidth = g2.getFontMetrics().stringWidth(noPathFound);
            g2.drawString(noPathFound, Main.MENU_WIDTH+(Main.FRAME_HEIGHT/2)-(stringWidth/2), (Main.FRAME_HEIGHT/2));
        }
    }

    public void drawGridLines(Graphics2D g2, Node[][] grid, int sideLength) {
        g2.setColor(Color.GRAY);
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
                    g2.setColor(new Color(this.r, this.g, this.b));
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
