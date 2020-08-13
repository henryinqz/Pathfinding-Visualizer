import java.util.*;

public class Pathfinder {
    // PROPERTIES
    private ArrayList<Node> path;
    private Node[][] grid;
    private Node startNode, endNode, currentNode;

    // CONSTRUCTOR
    public Pathfinder(Node[][] grid, Node startNode, Node endNode) {
        this.grid = grid;
        this.startNode = startNode;
        this.endNode = endNode;
    }

    // METHODS
    public ArrayList<Node> getShortestPath() { // Get shortest path
        if (this.grid == null || (!this.grid[endNode.getY()][endNode.getX()].isVisited() && !Node.isEqual(this.currentNode, this.endNode))) { // Didn't reach end path
            return null;
        } else {
            ArrayList<Node> shortestPath = new ArrayList<>();
            Node pathNode = this.grid[endNode.getY()][endNode.getX()]; // Get shortest path
            while (pathNode.getParent() != null) {
                pathNode = pathNode.getParent();
                shortestPath.add(pathNode);
            }
            Collections.reverse(shortestPath);
            return shortestPath;
        }
    }
    private ArrayList<Node> getNeighbors(Node node) { // Get neighbours of node in grid
        ArrayList<Node> neighbors = new ArrayList<>();
        int nodeX = node.getX();
        int nodeY = node.getY();

        // Check up, right, down, left + out of bound checks
        if (nodeY > 0) { // Add upper neighbour
            neighbors.add(this.grid[nodeY-1][nodeX]);
        }
        if (nodeY < this.grid.length-1) { // Add lower neighbour
            neighbors.add(this.grid[nodeY+1][nodeX]);
        }
        if (nodeX > 0) { // Add Left neighbour
            neighbors.add(this.grid[nodeY][nodeX-1]);
        }
        if (nodeX < this.grid[nodeY].length-1) { // Add right neighbour
            neighbors.add(this.grid[nodeY][nodeX+1]);
        }
        return neighbors;
    }
    private int getHeuristic(Node start, Node end) { // Calculate heuristic between two nodes (Manhattan distance)
        int heuristic = Math.abs(start.getX()-end.getX()) + Math.abs(start.getY()-end.getY());
        return heuristic;
    }

    // BREADTH-FIRST SEARCH
    public ArrayList<Node> bfs() {
        this.path = new ArrayList<>();
        Queue<Node> queue = new LinkedList<Node>();

        // BFS algorithm
        queue.add(this.startNode);
        this.grid[this.startNode.getY()][this.startNode.getX()].setVisited(true);
        while (!queue.isEmpty()) {
            if (this.grid[endNode.getY()][endNode.getX()].isVisited()) { // Exit method if endNode has been found
                System.out.println("BFS: End found; breaking");
                break;
            }

            this.currentNode = queue.poll();
            for (Node neighbor : getNeighbors(this.currentNode)) {
                if (neighbor != null && !neighbor.isVisited() && !neighbor.isBarrier()) {
                    queue.add(neighbor);
                    neighbor.setVisited(true);

                    neighbor.setParent(this.currentNode);
                    this.path.add(this.grid[neighbor.getY()][neighbor.getX()]);
                }
            }
        }

        return this.path;
    }

    // DEPTH-FIRST SEARCH
    public ArrayList<Node> dfs() {
        this.path = new ArrayList();
        dfsRecursive(this.startNode, null); // DFS algorithm
        return this.path;
    }
    private void dfsRecursive(Node currentNode, Node parentNode) {
        int currentX = currentNode.getX();
        int currentY = currentNode.getY();
        if (this.grid[endNode.getY()][endNode.getX()].isVisited()) { // Exit method if endNode has been found
            System.out.println("DFS: End found; returning");
            return;
        } else if (currentY < 0 || currentY >= this.grid.length || currentX < 0 || currentX >= this.grid[currentY].length || this.grid[currentY][currentX].isBarrier() || this.grid[currentY][currentX].isVisited()) { // Exit method if out of bounds or on closed node(?)
            return;
        }

        this.grid[currentY][currentX].setVisited(true);
        this.path.add(this.grid[currentY][currentX]);
        if (parentNode != null) {
            currentNode.setParent(parentNode);
        }

        try { // Up
            dfsRecursive(this.grid[currentY-1][currentX], currentNode); // DFS to upper node
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try { // Right
            dfsRecursive(this.grid[currentY][currentX+1], currentNode); // DFS to right node
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try { // Down
            dfsRecursive(this.grid[currentY+1][currentX], currentNode); // DFS to lower node
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try { // Left
            dfsRecursive(this.grid[currentY][currentX-1], currentNode); // DFS to left node
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }
    /*private void dfsRecursive(int currentX, int currentY) {
        if (this.grid[endNode.getY()][endNode.getX()].isVisited()) { // Exit method if endNode has been found
            System.out.println("DFS: End found; returning");
            return;
        } else if (currentY < 0 || currentY >= this.grid.length || currentX < 0 || currentX >= this.grid[currentY].length || this.grid[currentY][currentX].isBarrier() || this.grid[currentY][currentX].isVisited()) { // Exit method if out of bounds or on closed node(?)
            return;
        }

        this.grid[currentY][currentX].setVisited(true);
        this.path.add(this.grid[currentY][currentX]);

        this.grid[currentY-1][currentX].setParent(this.grid[currentY][currentX]);
        dfsRecursive(currentX, currentY - 1); // Up
        dfsRecursive(currentX + 1, currentY); // Right
        dfsRecursive(currentX, currentY + 1); // Down
        dfsRecursive(currentX - 1, currentY); // Left
    }*/

    // A* SEARCH ALGORITHM
    public ArrayList<Node> astar() {
        this.path = new ArrayList();

        // Initialize F/G/H costs of nodes
        for (int i=0; i<this.grid.length; i++) {
            for (int j=0; j<this.grid[i].length; j++) {
                this.grid[i][j].setG(Integer.MAX_VALUE);
                this.grid[i][j].setF(Integer.MAX_VALUE);
            }
        }
        this.startNode.setG(0);
        this.startNode.setH(getHeuristic(this.startNode, this.endNode));
        this.startNode.setF(this.startNode.getG() + this.startNode.getH());
//        this.endNode.setG(0);
//        this.endNode.setH(0);
//        this.endNode.setF(0);

        ArrayList<Node> openList = new ArrayList<>(); // Initialize ArrayLists of open & closed nodes
        ArrayList<Node> closedList = new ArrayList<>();

        // A* algorithm
        openList.add(this.startNode);
        while (!openList.isEmpty()) {
            this.currentNode = openList.get(0);
            for (int i=0; i<openList.size(); i++) { // Set currentNode to node on openList w/ lowest F-cost
                if (openList.get(i).getF() < this.currentNode.getF()) { // New F-cost is less than current F-cost
                    this.currentNode = openList.get(i);
                }
            }

            //if (this.grid[endNode.getY()][endNode.getX()].isVisited()) {
            if (Node.isEqual(this.currentNode, this.endNode)) { // Exit method if endNode has been found
                System.out.println("A*: End found; breaking");
                break;
            }

            openList.remove(this.currentNode); // Move current node from open -> closed list
            closedList.add(this.currentNode);

            for (Node neighbor : getNeighbors(this.currentNode)) {
                if (neighbor != null && !closedList.contains(neighbor) && !neighbor.isBarrier()) {
                    int tempG = this.currentNode.getG() + 1;
                    if (tempG < neighbor.getG()) {
                        neighbor.setParent(this.currentNode);
                        neighbor.setG(tempG);
                        neighbor.setF(tempG + getHeuristic(neighbor, this.endNode));
                        if (!openList.contains(neighbor)) {
                            openList.add(neighbor);
                        }
                    }

                    this.path.add(this.grid[neighbor.getY()][neighbor.getX()]);
                }
            }

            if (this.currentNode != this.startNode) {
                closedList.add(this.currentNode);
            }
        }

        this.startNode.setStart();
        this.endNode.setEnd();

        return this.path;
    }

    // DIJKSTRA'S ALGORITHM
    public ArrayList<Node> dijkstra() {
        this.path = new ArrayList();

        return this.path;
    }
}
