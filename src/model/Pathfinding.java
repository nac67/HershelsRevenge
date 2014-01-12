package model;

import java.awt.Point;
import java.util.LinkedList;

/**
 * A class with static methods for pathfinding. The pathfinding must be a 2d Array
 * of binary Integers: 0 for open, 1 for wall. It will find the shortest path, or an
 * optimal path using A*.
 * 
 * There are also 4 levels of diagonal movement acceptance:
 * 
 *  Level 0 - Do not consider diagonal movement at all. An L-shape will have
 *  the same distance as a hypotenuse (since the hypotenuse will be made of zig-zags)
 *                 
 *  S o o o o o o  
 *              o  
 *              o  
 *  X X X X     o  
 *        X     o  
 *        X     o  
 *        X     o  
 *        X     E  
 * 
 *  
 *  Level 1 - The path is determined with diagonal movement, but is then "filled in"
 *  so that the final result contains only cardinal directions. This will cause zig-zag diagonals
 *  
 *  S o            
 *    o o          
 *      o o o      
 *  X X X X o      
 *        X o      
 *        X o o    
 *        X   o o  
 *        X     E  
 *        
 *  
 *  Level 2 - The path is determined with diagonal movement, and is only filled in when going around
 *  corners to prevent clipping around corners.
 *  
 *  S              
 *    o            
 *      o o o      
 *  X X X X o      
 *        X o      
 *        X o      
 *        X   o    
 *        X     E  
 *  
 *  Level 3 - The path is determined with diagonal movement, and is not filled in at all.
 *  
 *  S              
 *    o            
 *      o o        
 *  X X X X o      
 *        X o      
 *        X o      
 *        X   o    
 *        X     E  
 *        
 * @author Nick Cheng
 *
 */
public class Pathfinding {
    private static int ROWS;
    private static int COLS;
    private static boolean[][] walls;
    private static Point start;
    private static Point end;
    
    /**This lets it consider taking a diagonal at a slightly higher cost
     * It will not cut through a diagonal spot if it is a super pinchy point
     */
    private static boolean TAKE_DIAGONAL;
    /**
     * This will have it fill in the diagonal spots when walking around corners as to not cut corners
     */
    private static boolean FILL_DIAGONAL;
    /**
     * This will have it fill in the diagonal spots so that the entire path is only cardinal directions
     */
    private static boolean FILL_DIAGONAL_ALWAYS;

    /**
     * Default path-finding method with all options available.
     * @param obstacles A 2-D binary int array. 0 for open, 1 for wall.
     * @param start A starting point (x,y) i.e. (column,row)
     * @param end An ending point (x,y) i.e. (column,row)
     * @param useHeuristic Heuristic makes it more efficient but less accurate
     * @param diag An integer 0,1,2,3 representing level of diagonal acceptance.
     *      See class specification for more details
     * @return A linked list representing path from beginning to end. (path includes endpoint)
     */
    public static LinkedList<Point> findPath(boolean[][] obstacles, Point start, Point end, Boolean useHeuristic, int diag){
        ROWS = obstacles.length;
        COLS = obstacles[0].length;
        walls = obstacles;
        
        Pathfinding.start = start;
        Pathfinding.end = end;
        
        if(diag == 0){
            //don't consider diagonal at all
            TAKE_DIAGONAL = false;
            FILL_DIAGONAL = false;
            FILL_DIAGONAL_ALWAYS = false;
        }else if(diag == 1){
            //use diagonal but fill in, so end result is only cardinal directions
            TAKE_DIAGONAL = true;
            FILL_DIAGONAL = true;
            FILL_DIAGONAL_ALWAYS = true;
        }else if(diag == 2){
            //use diagonal fill in only spots to prevent cutting corners
            TAKE_DIAGONAL = true;
            FILL_DIAGONAL = true;
            FILL_DIAGONAL_ALWAYS = false;
        }else if(diag == 3){
            //use diagonal and don't fill in anything
            TAKE_DIAGONAL = true;
            FILL_DIAGONAL = false;
            FILL_DIAGONAL_ALWAYS = false;
        }else{
            System.out.println("diag must be between 0..3");
            return null;
        }
        
        if(useHeuristic){
            return astar();
        }else{
            return dijkstra();
        }
    }
    
    /**
     * A simple path-finding method that uses heuristic and no diagonalization.
     * @param obstacles A 2-D binary int array. 0 for open, 1 for wall.
     * @param start A starting point (x,y) i.e. (column,row)
     * @param end An ending point (x,y) i.e. (column,row)
     * @return A linked list representing path from beginning to end. (path includes endpoint)
     */
    public static LinkedList<Point> findPath(boolean[][] obstacles, Point start, Point end){
        return findPath(obstacles, start, end, true, 0);
    }
    
    /**
     * A simple path-finding method that uses heuristic and fills diagonals
     * @param obstacles A 2-D binary int array. 0 for open, 1 for wall.
     * @param start A starting point (x,y) i.e. (column,row)
     * @param end An ending point (x,y) i.e. (column,row)
     * @return A linked list representing path from beginning to end. (path includes endpoint)
     */
    public static LinkedList<Point> findPathFillDiag(boolean[][] obstacles, Point start, Point end){
        return findPath(obstacles, start, end, true, 1);
    }
    
    /**
     * A simple path-finding method that uses heuristic and non-corner cutting diagonalization
     * @param obstacles A 2-D binary int array. 0 for open, 1 for wall.
     * @param start A starting point (x,y) i.e. (column,row)
     * @param end An ending point (x,y) i.e. (column,row)
     * @return A linked list representing path from beginning to end. (path includes endpoint)
     */
    public static LinkedList<Point> findPathWithDiag(boolean[][] obstacles, Point start, Point end){
        return findPath(obstacles, start, end, true, 2);
    }
    
    
    /**
     * Dijkstra's algorithm.
     * @return The guaranteed shortest path from start to end.
     */
    private static LinkedList<Point> dijkstra() {
        double[][] dist = new double[walls.length][walls[0].length];
        boolean[][] visited = new boolean [walls.length][walls[0].length];
        Point[][] previous = new Point [walls.length][walls[0].length];
        LinkedList<Point> searchQueue = new LinkedList<Point>();
        
        //Initialize arrays
        for(int i=0;i<walls.length;i++){
            for (int j=0;j<walls[i].length;j++){
                dist[i][j] = Double.MAX_VALUE;
                visited[i][j] = false;
                previous[i][j] = null;
            }
        }
        
        //Prepare start point
        dist[start.y][start.x] = 0;
        searchQueue.add(new Point(start.x,start.y));
        
        while(searchQueue.size()>0){
            //Find closest point
            Point smallest = searchQueue.getFirst(); //u
            double smallestDist = dist[smallest.y][smallest.x];
            for (Point p : searchQueue){
                double newDist = dist[p.y][p.x];
                if(newDist<smallestDist){
                    smallest = p;
                    smallestDist = newDist;
                }
            }    
            
            //End search if found end point
            if (smallest.equals(end)){
                return reconstructPath(previous);
            }
            
            searchQueue.remove(smallest);
            visited[smallest.y][smallest.x] = true;
            
            //Process neighbors
            LinkedList<Point> neighbors = getNeighbors(smallest);
            for(Point p : neighbors){
                double weight = (isDiagonal(p,smallest) ? 1.41 : 1.0);
                double alt = smallestDist + weight;
                if (alt < dist[p.y][p.x]){
                    dist[p.y][p.x] = alt;
                    previous[p.y][p.x] = smallest;
                    if(!visited[p.y][p.x]){
                        searchQueue.add(p);
                    }
                }
            }
        }
        
        //Reconstruct path
        LinkedList<Point> daPath = new LinkedList<Point>();
        Point u = end;
        while (previous[u.y][u.x] != null){
            daPath.add(u);
            Point prev = previous[u.y][u.x];
            if(FILL_DIAGONAL && isDiagonal(u,prev)) {
                    if(FILL_DIAGONAL_ALWAYS || !(isOpen(u.x,prev.y) && isOpen(prev.x,u.y))){
                        if(isOpen(u.x,prev.y)){
                            daPath.add(new Point(u.x,prev.y));
                        }else{
                            daPath.add(new Point(prev.x,u.y));
                        }
                    }
                }
            u = prev;
        }
        
        return null;
    }

    /**
     * A* Pathfinding algorithm. Uses Euclidean distance as heuristic.
     * @return A path from start to end. Not guaranteed to be exact shortest.
     */
    private static LinkedList<Point> astar() {
        //distance from start
        double[][] dist = new double[walls.length][walls[0].length];
        //distance from start to point, plus estimated distance from point to end
        double[][] fscore = new double[walls.length][walls[0].length];
        boolean[][] visited = new boolean [walls.length][walls[0].length];
        Point[][] previous = new Point [walls.length][walls[0].length];
        LinkedList<Point> searchQueue = new LinkedList<Point>();
        
        //Initialize data structures
        for(int i=0;i<walls.length;i++){
            for (int j=0;j<walls[i].length;j++){
                dist[i][j] = Double.MAX_VALUE;
                fscore[i][j] = Double.MAX_VALUE;
                visited[i][j] = false;
                previous[i][j] = null;
            }
        }
        
        //prepare first point
        dist[start.y][start.x] = 0;
        searchQueue.add(new Point(start.x,start.y));
        
        while(searchQueue.size()>0){
            //find smallest f-score, not distance
            Point smallest = searchQueue.getFirst(); //u
            double smallestFscore = fscore[smallest.y][smallest.x];
            for (Point p : searchQueue){
                double newDist = fscore[p.y][p.x];
                if(newDist<smallestFscore){
                    smallest = p;
                    smallestFscore = newDist;
                }
            }    
            
            //End search if found end point
            if (smallest.equals(end)){
                return reconstructPath(previous);
            }
            
            searchQueue.remove(smallest);
            visited[smallest.y][smallest.x] = true;
            
            //Process neighbors
            LinkedList<Point> neighbors = getNeighbors(smallest);
            for(Point p : neighbors){
                double weight = (isDiagonal(p,smallest) ? 1.41 : 1.0);
                double alt = dist[smallest.y][smallest.x] + weight;
                double altF = alt + eDistance(smallest,end);
                if (alt < dist[p.y][p.x]){
                    dist[p.y][p.x] = alt;
                    fscore[p.y][p.x] = altF;
                    previous[p.y][p.x] = smallest;
                    if(!visited[p.y][p.x]){
                        searchQueue.add(p);
                    }
                }
            }
        }
        
        //Reconstruct path
        LinkedList<Point> daPath = new LinkedList<Point>();
        Point u = end;
        while (previous[u.y][u.x] != null){
            daPath.add(u);
            Point prev = previous[u.y][u.x];
            if(FILL_DIAGONAL && isDiagonal(u,prev)) {
                    if(FILL_DIAGONAL_ALWAYS || !(isOpen(u.x,prev.y) && isOpen(prev.x,u.y))){
                        if(isOpen(u.x,prev.y)){
                            daPath.add(new Point(u.x,prev.y));
                        }else{
                            daPath.add(new Point(prev.x,u.y));
                        }
                    }
                }
            u = prev;
        }
        
        return null;
    }
    
    /**
     * Reconstructs the path from the beginning to end
     * @return
     */
    private static LinkedList<Point> reconstructPath(Point[][] previous){
        LinkedList<Point> finalPath = new LinkedList<Point>();
        Point u = end;
        while (previous[u.y][u.x] != null){
            finalPath.add(0,u);
            Point prev = previous[u.y][u.x];
            if(FILL_DIAGONAL && isDiagonal(u,prev)) {
                    if(FILL_DIAGONAL_ALWAYS || !(isOpen(u.x,prev.y) && isOpen(prev.x,u.y))){
                        if(isOpen(u.x,prev.y)){
                            finalPath.add(0,new Point(u.x,prev.y));
                        }else{
                            finalPath.add(0,new Point(prev.x,u.y));
                        }
                    }
                }
            u = prev;
        }
        
        
        
        return finalPath;
    }
    
    /**
     * Is a cell an open spot on board?
     * I.E. Not off the board, and not a wall?
     * @param x X coordinate of point
     * @param y Y coordinate of point
     * @return
     */
    private static boolean isOpen(int x, int y){
        if (x<0 || y < 0) return false;
        if (x >= COLS || y >= ROWS) return false;
        return !walls[y][x];
    }
    
    /**
     * Finds neighbors of a given cell. If diagonal is on,
     * it can consider all 8 neighbors, otherwise just 4
     * cardinal neighbors
     * @param p Cell to find neighbors for
     * @return A linked list of neighbors
     */
    private static LinkedList<Point> getNeighbors (Point p) {
        LinkedList<Point> n = new LinkedList<Point>();
    
        if(isOpen(p.x-1,p.y))
            n.add(new Point(p.x-1,p.y));
        if(isOpen(p.x,p.y-1))
            n.add(new Point(p.x,p.y-1));
        if(isOpen(p.x+1,p.y))
            n.add(new Point(p.x+1,p.y));
        if(isOpen(p.x,p.y+1))
            n.add(new Point(p.x,p.y+1));
        
        if(TAKE_DIAGONAL){
            if(isOpen(p.x-1,p.y)||isOpen(p.x,p.y-1)){
                if (isOpen(p.x-1,p.y-1))
                    n.add(new Point(p.x-1,p.y-1));
            }
            if(isOpen(p.x+1,p.y)||isOpen(p.x,p.y-1)){
                if (isOpen(p.x+1,p.y-1))
                    n.add(new Point(p.x+1,p.y-1));
            }
            if(isOpen(p.x-1,p.y)||isOpen(p.x,p.y+1)){
                if (isOpen(p.x-1,p.y+1))
                    n.add(new Point(p.x-1,p.y+1));
            }
            if(isOpen(p.x+1,p.y)||isOpen(p.x,p.y+1)){
                if (isOpen(p.x+1,p.y+1))
                    n.add(new Point(p.x+1,p.y+1));
            }
        }
       
        
        return n;
    }
    
    /**
     * Returns false if two cells are adjacent, returns
     * true if two cells are diagonal.
     * PRECONDITION: Cells have to be touching by edge or corner
     * @param p1 First cell
     * @param p2 Second cell
     * @return Whether cells are diagonal
     */
    private static boolean isDiagonal (Point p1, Point p2){
        boolean sameX = (p2.x - p1.x) == 0;
        boolean sameY = (p2.y - p1.y) == 0;
        return !sameX && !sameY;
    }
    
    /**
     * Euclidean distance between two points
     * @param p1 First point
     * @param p2 Second point
     * @return Double value of Euclidean distance
     */
    private static double eDistance (Point p1, Point p2){
        int xdis = p2.x - p1.x;
        int ydis = p2.y - p1.y;
        return Math.sqrt(xdis*xdis + ydis*ydis);
    }

}
