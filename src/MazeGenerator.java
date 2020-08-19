import java.util.ArrayList;

public class MazeGenerator {
    // PROPERTIES
    private static final boolean HORIZONTAL=false, VERTICAL=true;

    // METHODS
    // Recursive Division
    public static void recursiveDivision(Node[][] grid, int startX, int startY, int endX, int endY) { // Implemented based on http://weblog.jamisbuck.org/2011/1/12/maze-generation-recursive-division-algorithm
        // Clear old grid prior to initial call, leaving only start/end node (if they exist)
        // Initial call: recursive_division(grid, 0, 0, gridWidth-1, gridWidth-1)

        // Set dimensions using start/end coordinates
        int width = endX - startX;
        int height = endY - startY;

        // Return if width or height is less than 2 (completed)
        if (width < 2 || height < 2) {
            System.out.println("Returning");
            return;
        }

        // Choose orientation to split (horizontal/vertical)
        boolean orientation;
        if (width < height) { // If taller, set orientation to horizontal
            orientation = HORIZONTAL;
        } else if (width > height) { // If wider, set orientation to vertical
            orientation = VERTICAL;
        } else { // If square (width==height), randomly select orientation
            orientation = (int)(Math.random()*2) == 0 ? HORIZONTAL : VERTICAL;
        }

        /* Determine where wall is drawn:
        - Horizontal split: wallX is 0, wallY is randomly selected within height
        - Vertical split: wallX is randomly selected within width, wallY is 0 */
        //int wallX = startX + (orientation == HORIZONTAL ? 0 : (int)(Math.random()*(width-2))); // TODO: Width-2?
        //int wallY = startY + (orientation == HORIZONTAL ? (int)(Math.random()*(height-2)) : 0);

        /* Determine where hole/passage in wall is:
        - Horizontal split: holeX is randomly selected within width, holeY is 0
        - Vertical split: holeX is 0, holeY is randomly selected within height */
        //int holeX = wallX + (orientation == HORIZONTAL ? (int)(Math.random()*width) : 0);
        //int holeY = wallY + (orientation == HORIZONTAL ? 0 : (int)(Math.random()*height));

        // Assuming startX/startY is empty corner.
        // wallX:
        // - Horizontal split: startX
        // - Vertical split: (startX+1) + (2 * randInRange(0,width/2)) < endX
        // wallY:
        // - Horizontal split: (startY+1) += 2
        // - Vertical split: startY
        int wallX=0, wallY=0, holeX=0, holeY=0;
        boolean loopBack = true;
        if (orientation == HORIZONTAL) { // Horizontal split
//            ArrayList<Integer> possibleRowIndexes = new ArrayList<>();
//            for (int row=1; row<endY; row+=2) {
//                // Check if possible row will block any holes
//                if ((startX > 0 && grid[startY+row][startX-1].isEmpty()) ||
//                        (endX < grid.length-1) && grid[startY+row][endX+1].isEmpty()) { // Empty node on left or right of row
//                    System.out.println("Prevented row from blocking hole");
//                } else {
//                    possibleRowIndexes.add(row);
//                }
//            }
//            System.out.println(possibleRowIndexes);
//wallY = startY + possibleRowIndexes.get((int)(Math.random()*possibleRowIndexes.size()));

            int possibleRows = 0;
            for (int row=1; row<height; row+=2) {
                possibleRows++;
            }

            while (loopBack == true) {
                wallX = startX;
                wallY = (startY + 1) + (2 * (int) (Math.random() * possibleRows));

                // Hole
                boolean leftHole = false, rightHole = false;
                if (wallX > 0 && grid[wallY][wallX - 1].isEmpty()) {
                    leftHole = true;
                } else if (endX < grid[0].length-1 && grid[wallY][endX + 1].isEmpty()) {
                    rightHole = true;
                }

                if (leftHole && rightHole) {
                    // Loop back and regenerate different wallY
                    loopBack = true;
                    System.out.println("[H] Looping back");
                } else if (leftHole && !rightHole) {
                    holeX = wallX;
                    loopBack = false;
                    System.out.println("Left hole");
                } else if (!leftHole && rightHole) {
                    holeX = endX;
                    loopBack = false;
                    System.out.println("right hole");
                } else {
                    // Randomly generate hole
                    holeX = wallX + (int) (Math.random() * width);
                    loopBack = false;
                    System.out.println("Nothing");
                }
            }
            holeY = wallY;
        } else { // Vertical split
            int possibleCols = 0;
            for (int col=1; col<width; col+=2) {
                possibleCols++;
            }

            while (loopBack == true) {
                wallX = (startX + 1) + (2 * (int) (Math.random() * possibleCols));
                wallY = startY;

                // Hole
                boolean topHole = false, bottomHole = false;
                if (wallY > 0 && grid[wallY-1][wallX].isEmpty()) {
                    topHole = true;
                }
                if (endY < grid.length-1 && grid[endY+1][wallX].isEmpty()) {
                    bottomHole = true;
                }

                if (topHole && bottomHole) {
                    // Loop back and regenerate different wallX
                    if (possibleCols > 1) {
                        loopBack = true;
                        System.out.println("[V] Looping back");
                    } else {
                        loopBack = false;
                        System.out.println("[V] Couldn't loop back; not enough possible columns");
                    }
                } else if (topHole && !bottomHole) {
                    holeY = wallY;
                    loopBack = false;
                    System.out.println("Top hole");
                } else if (!topHole && bottomHole) {
                    holeY = endY;
                    loopBack = false;
                    System.out.println("Bottom hole");
                } else {
                    // Randomly generate hole
                    holeY = wallY + (int)(Math.random()*height);
                    loopBack = false;
                    System.out.println("Nothing");
                }
            }
            holeX = wallX;
        }



        // Determine length of wall
        int length = orientation == HORIZONTAL ? width : height;

        // Set barriers on grid
        for (int i=0; i <= length; i++) {
            if (!(wallX == holeX && wallY == holeY)) { // Prevent setting hole as barrier
                grid[wallY][wallX].setBarrier();
            }

            if (i != length) {
                if (orientation == HORIZONTAL) {
                    wallX++;
                } else {
                    wallY++;
                }
            }
        }

        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Recursively call method
        if (orientation == HORIZONTAL) { // Horizontally split
            //int newY = wallY-startY+1;
            //recursiveDivision(grid, startX, startY, endX, newY); // Search above
            //recursiveDivision(grid, startX, newY, endX, endY); // Search below

            recursiveDivision(grid, startX, startY, endX, wallY-1); // Search above: (startX, startY) -> (endX, wallY)
            recursiveDivision(grid, startX, wallY+1, endX, endY); // Search below: (startX, wallY+1) -> (endX, endY)
        } else { // Vertically split
            //int newX = wallX-startX+1;
            //recursiveDivision(grid, startX, startY, newX, endY); // Search left side
            //recursiveDivision(grid, newX, startY, endX, endY); // Search right side

            recursiveDivision(grid, startX, startY, wallX-1, endY); // Search left: (startX, startY) -> (wallX-1, endY)
            recursiveDivision(grid, wallX+1, startY, endX, endY); // Search right: (wallX+1, startY) -> (endX, endY)
        }

//        int newX = startX, newY = startY;
//        int newEndX = orientation == HORIZONTAL ? width : wallX-startX+1;
//        int newEndY = orientation == HORIZONTAL ? wallY-startY+1 : height;
//        recursiveDivision(grid, newX, newY, newEndX, newEndY);
//
//        newX = orientation == HORIZONTAL ? startX : wallX+1;
//        newY = orientation == HORIZONTAL ? wallY+1 : startY;
//        newEndX = orientation == HORIZONTAL ? width : startX+width-wallX-1;
//        newEndY = orientation == HORIZONTAL ? startY+height-wallY-1 : height;
//        recursiveDivision(grid, newX, newY, newEndX, newEndY);
    }

    // CONSTRUCTOR (?)
}
