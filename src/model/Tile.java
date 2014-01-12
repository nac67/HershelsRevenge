package model;

import java.awt.Point;

public class Tile extends MovieClip{
    public enum Direction{
        UP, DOWN, LEFT, RIGHT;
    }
    
    public int row;
    public int col;
    
    public Direction dir = Direction.UP;
    
    //helper variables when sliding character
    //bit = move distance / move time
    public double bitX;
    public double bitY;
    
    

    public Tile(String path, boolean multipleAnims) {
        super(path, multipleAnims);
    }

    public Tile(String path) {
        super(path);
    }

    public int compareDepth(Tile t) {
        //-1 if less than t
        //0 if same
        //1 if greater than t
        if(row == t.row){
            return 0;
        }
        return (row<t.row ? -1 : 1);
    }
    
    public String toString(){
        return Integer.toString(id)+": ("+Integer.toString(col)+", "+Integer.toString(row)+")";
    }
    
    public Point getPos (){
        return new Point(col,row);
    };
    
    public void moveABit(){
        x += bitX;
        y += bitY;
    }

}
