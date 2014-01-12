package model.utils;

public class DPoint {
    public double x;
    public double y;

    public DPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public String toString(){
        return "("+x+", "+y+")";
    }

}
