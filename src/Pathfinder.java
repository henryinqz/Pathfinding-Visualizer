import java.util.*;

public class Pathfinder {
    // PROPERTIES
    private ArrayList<Node> open_list, closed_list, path;
    private Node[][] grid;
    private Node startNode, endNode, currentNode;

    // METHODS
    // Get shortest path
    public ArrayList<Node> getShortestPath() {
        if (this.grid == null || !this.grid[endNode.getY()][endNode.getX()].isVisited()) { // Didn't reach end path
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

    // Breadth-first search
    public ArrayList<Node> bfs() {
        this.path = new ArrayList<>();
        Queue<Node> queue = new LinkedList<Node>();

        // BFS
        queue.add(this.startNode);
        this.grid[this.startNode.getY()][this.startNode.getX()].setVisited(true);
        while (!queue.isEmpty()) {
            if (this.grid[endNode.getY()][endNode.getX()].isVisited()) { // Exit method if endNode has been found
                System.out.println("BFS: End found; breaking");
                break;
            }

            Node temp = queue.poll();
            for (Node neighbor : getNeighbors(temp)) {
                if (neighbor != null && !neighbor.isVisited() && !neighbor.isBarrier()) {
                    queue.add(neighbor);
                    neighbor.setVisited(true);

                    neighbor.setParent(temp);
                    this.path.add(this.grid[neighbor.getY()][neighbor.getX()]);
                }
            }
        }

        return this.path;
    }
    private ArrayList<Node> getNeighbors(Node node) {
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

    // Depth-first search
    public ArrayList<Node> dfs() {
        this.path = new ArrayList();

//        dfsRecursive(this.startNode.getX(), this.startNode.getY());
        dfsRecursive(this.startNode, null);
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
//    private void dfsRecursive(int currentX, int currentY) {
//        if (this.grid[endNode.getY()][endNode.getX()].isVisited()) { // Exit method if endNode has been found
//            System.out.println("DFS: End found; returning");
//            return;
//        } else if (currentY < 0 || currentY >= this.grid.length || currentX < 0 || currentX >= this.grid[currentY].length || this.grid[currentY][currentX].isBarrier() || this.grid[currentY][currentX].isVisited()) { // Exit method if out of bounds or on closed node(?)
//            return;
//        }
//
//        this.grid[currentY][currentX].setVisited(true);
//        this.path.add(this.grid[currentY][currentX]);
//
//        this.grid[currentY-1][currentX].setParent(this.grid[currentY][currentX]);
//        dfsRecursive(currentX, currentY - 1); // Up
//        dfsRecursive(currentX + 1, currentY); // Right
//        dfsRecursive(currentX, currentY + 1); // Down
//        dfsRecursive(currentX - 1, currentY); // Left
//    }

    /*public void astar(int[][] maze, Node start, Node end) {
        // Create start & end nodes
        this.start_node = start;
        start_node.setF(0);
        start_node.setG(0);
        start_node.setH(0);

        this.end_node = end;
        end_node.setF(0);
        end_node.setG(0);
        end_node.setH(0);

        // Initialize ArrayLists of open & closed nodes
        open_list = new ArrayList<>();
        closed_list = new ArrayList<>();

        // Add starting node to list of open nodes
        open_list.add(start_node);

        while (!open_list.isEmpty()) {
            current_node = open_list.get(0);
            int current_index = 0;
            for (int i = 0; i < open_list.size(); i++) { // Iterate through open nodes to find lowest node w/ F-cost
                if (current_node.getF() > open_list.get(i).getF()) { // If new F-cost is less than current F-cost
                    current_node = open_list.get(i); // Update current node to new node
                    current_index = i;
                }
            }

            // Move current node from open -> closed list
            open_list.remove(current_index);
            closed_list.add(current_node);

            if (Node.isEqual(current_node, end_node) == true) {

            }

        }
    }*/

    // CONSTRUCTOR
    public Pathfinder(Node[][] grid, Node startNode, Node endNode) {
        this.grid = grid;
        this.startNode = startNode;
        this.endNode = endNode;
    }
}
