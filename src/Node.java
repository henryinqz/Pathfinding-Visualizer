import java.awt.*;

public class Node {
    // PROPERTIES
    private int x, y, g, h, f;
    private Node parent;

    private Color color; // WHITE=empty, BLACK=barrier, GREEN=start, RED=end
    private final Color EMPTY_NODE = Color.WHITE, BARRIER_NODE = Color.BLACK, START_NODE = Color.GREEN, END_NODE = Color.RED;
    private final Color CLOSED_NODE = new Color(0,200,255);
    private boolean visited = false;

    // METHODS
    public static boolean isEqual(Node start, Node end) {
        if (start.getX() == end.getX() && start.getY() == end.getY()) {
            return true;
        } else {
            return false;
        }
    }

    // Getters
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getG() {
        return g;
    }
    public int getH() {
        return h;
    }
    public int getF() {
        return f;
    }
    public Node getParent() {
        return parent;
    }
    public Color getColor() {
        return color;
    }
    public boolean isEmpty() {
        return this.color == EMPTY_NODE;
    }
    public boolean isBarrier() {
        return this.color == BARRIER_NODE;
    }
    public boolean isStart() {
        return this.color == START_NODE;
    }
    public boolean isEnd() {
        return this.color == END_NODE;
    }
    public boolean isClosed() {
        return this.color == CLOSED_NODE;
    }
    public boolean isVisited() {
        return this.visited;
    }


    // Setters
    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void setG(int g) {
        this.g = g;
    }
    public void setH(int h) {
        this.h = h;
    }
    public void setF(int f) {
        this.f = f;
    }
    public void setParent(Node parent) {
        this.parent = parent;
    }
    public void setEmpty() {
        this.color = EMPTY_NODE;
    }
    public void setBarrier() {
        this.color = BARRIER_NODE;
    }
    public void setStart() {
        this.color = START_NODE;
    }
    public void setEnd() {
        this.color = END_NODE;
    }
    public void setClosed() {
        this.color = CLOSED_NODE;
    }
    public void setVisited(boolean isVisited) { this.visited = isVisited; }

    // CONSTRUCTOR
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.color = EMPTY_NODE; // Default node to empty
    }
    public Node(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        if (color == EMPTY_NODE || color == BARRIER_NODE || color == START_NODE || color == END_NODE || color == CLOSED_NODE) { // Restrict node colours
            this.color = color;
        } else { // If node color is not empty/barrier/start/end, default to empty
            this.color = EMPTY_NODE;
        }
    }
}
