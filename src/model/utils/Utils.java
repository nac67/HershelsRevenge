package model.utils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import model.Tile;

public class Utils {
    
    public static void p(String s){
        System.out.println(s);
    }

    public static boolean circle_collision (double x1, double y1, double r1, 
                                            double x2, double y2, double r2){
        double distance = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
        return distance<= r1+r2;
    }
    
    public static boolean circle_collision (int x1, int y1, int r1, 
                                            int x2, int y2, int r2){
        double distance = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
        return distance<= r1+r2;
    }
    
    public static boolean circle_collision (Point p1, int r1, Point p2, int r2){
        double distance = Math.sqrt((p2.x-p1.x)*(p2.x-p1.x) + (p2.y-p1.y)*(p2.y-p1.y));
        return distance<= r1+r2;
    }
    
    public static boolean rect_collision (Point p1, int w1, int h1, Point p2, int w2, int h2){
        return !(p1.x > p2.x+w2 || p1.x+w1 < p2.x || p1.y > p2.y+h2 || p1.y+h1 < p2.y);
    }
    
    public static Point addPoints(Point p1, Point p2){
        return new Point(p1.x+p2.x,p1.y+p2.y);
    }
    
    public static <E> void swap(List<E> a, int i, int j) {
        E tmp = a.get(i);
        a.set(i, a.get(j));
        a.set(j, tmp);
    }
    
    public static boolean isInList(Tile t, ArrayList<Point> lst) {
        boolean result = false;
        for(Point p : lst){
            if(t.row == p.y && t.col == p.x) result = true;
        }
        return result;
    }
    
    
    public static boolean isInList(Point point, ArrayList<Point> lst) {
        boolean result = false;
        for(Point p : lst){
            if(point.y == p.y && point.x == p.x) result = true;
        }
        return result;
    }
    
    public static boolean isInTileList(Tile t, ArrayList<Tile> lst) {
        boolean result = false;
        for(Tile t2 : lst){
            if(t.row == t2.row && t.col == t2.col) result = true;
        }
        return result;
    }
    
    public static void print2DArray (boolean[][] a){
        for(int i=0;i<a.length;i++){
            System.out.print("\n");
            for(int j=0;j<a[i].length;j++){
                String bit = (a[i][j]? "X ":". ");
                System.out.print(bit);
            }
        }
    }
    
    public static String cd = null;
    public static String currentDir() {
        return cd;
        
    }

}
