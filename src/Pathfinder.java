import java.util.*;

public class Pathfinder {
    // PROPERTIES
    private ArrayList<Node> open_list, closed_list, searchPath, shortPath;
    private Node[][] grid;
    private Node startNode, endNode, currentNode;

    // METHODS
    // Breadth-first serach
    public ArrayList<ArrayList<Node>> bfs() {
        this.searchPath = new ArrayList<>();
        this.shortPath = new ArrayList<>();
        Queue<Node> queue = new LinkedList<Node>();

        // BFS
        queue.add(this.startNode);
        this.grid[this.startNode.getY()][this.startNode.getX()].setVisited(true);
        while (!queue.isEmpty()) {
            if (this.grid[endNode.getY()][endNode.getX()].isVisited()) { // Exit method if endNode has been found
                System.out.println("BFS: End found; breaking");

                Node pathNode = this.grid[endNode.getY()][endNode.getX()]; // Get shortest path
                while (pathNode.getParent() != null) {
                    pathNode = pathNode.getParent();
                    this.shortPath.add(pathNode);
                }
                Collections.reverse(this.shortPath);

                break;
            }

            Node temp = queue.poll();
            for (Node neighbor : getNeighbors(temp)) {
                if (neighbor != null && !neighbor.isVisited() && !neighbor.isBarrier()) {
                    queue.add(neighbor);
                    neighbor.setVisited(true);

                    neighbor.setParent(temp);
                    this.searchPath.add(this.grid[neighbor.getY()][neighbor.getX()]);
                }
            }
        }

        return new ArrayList<>(Arrays.asList(this.searchPath, this.shortPath));
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
        this.searchPath = new ArrayList();
        dfsRecursive(this.startNode.getX(), this.startNode.getY());

        return this.searchPath;
    }
    private void dfsRecursive(int currentX, int currentY) {
        if (this.grid[endNode.getY()][endNode.getX()].isVisited()) { // Exit method if endNode has been found
            System.out.println("DFS: End found; returning");
            return;
        } else if (currentY < 0 || currentY >= this.grid.length || currentX < 0 || currentX >= this.grid[currentY].length || this.grid[currentY][currentX].isBarrier() || this.grid[currentY][currentX].isVisited()) { // Exit method if out of bounds or on closed node(?)
            return;
        }

        this.grid[currentY][currentX].setVisited(true);
        this.searchPath.add(this.grid[currentY][currentX]);

        dfsRecursive(currentX, currentY - 1); // Up
        dfsRecursive(currentX + 1, currentY); // Right
        dfsRecursive(currentX, currentY + 1); // Down
        dfsRecursive(currentX - 1, currentY); // Left
    }

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
