package model.tileboard;

import java.awt.Point;
import java.io.BufferedReader;
//import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

import model.Constants;
import model.Model;
import model.MovieClip;
import model.Tile;
import model.utils.DPoint;

public class GameTiles {
    
    public int rows;
    public int cols;
    public int width;
    public int height;
    public int skewX;
    public int skewY;
    public int visualX;
    public int visualY;
    
    public MovieClip[][] background;
    public MovieClip[][] foreground;
    public boolean[][] walls;
    
    Model model;

    public GameTiles(Model model, int width, int height, int skewX, int skewY, int visualX, int visualY) {
        this.model = model;
        this.width = width;
        this.height = height;
        this.skewX = skewX;
        this.skewY = skewY;
        this.visualX = visualX;
        this.visualY = visualY;
    }
    
    public GameTiles(Model model, int width, int height, int skewX, int skewY) {
        this.model = model;
        this.width = width;
        this.height = height;
        this.skewX = skewX;
        this.skewY = skewY;
        this.visualX = 0;
        this.visualY = 0;
    }
    
    public GameTiles(Model model,int width, int height) {
        this.model = model;
        this.width = width;
        this.height = height;
        this.skewX = 0;
        this.skewY = 0;
        this.visualX = 0;
        this.visualY = 0;
    }
    
    private String numToBGTile (int i){
        switch(i){
            case 0:
                return "";
            case 1:
                return "images/bgtiles/grass4.png";
            case 2:
                return "images/bgtiles/dirt2.png";
            case 3:
                return "images/bgtiles/tut01.png";
            case 4:
                return "images/bgtiles/tut02.png";
            case 5:
                return "images/bgtiles/tut03.png";
            case 6:
                return "images/bgtiles/tut04.png";
        }
        return "";
    }
    
    /**
     * This will read a file which describes the layout of the background
     * tiles, it will then add those tiles to layer 0 of the display list
     * @param path
     */
    public void setBackground(String path){
        System.out.println("reading background: "+path);
        LinkedList<String> cells = parseFile(path);
        int currRow = 0;
        int currCol = 0;
        for (String textRow : cells){
            String[] textCells = textRow.split(" ");
            currCol = 0;
            for(String textCell : textCells){
                if(!textCell.equals("")){
                    try{
                        int cell = Integer.parseInt(textCell);
                        
                        if(cell > 0){
                            String im = numToBGTile(cell);
                             
                            MovieClip bgcell = new MovieClip(im);
                            Point position = tileToXY(currCol,currRow);
                            bgcell.x = position.x;
                            bgcell.y = position.y;
                            if(cell>=3){
                                bgcell.setOrigin(360, 0);
                            }
                            model.addChild(bgcell, 0);
                        }
                    }catch(NumberFormatException e){
                        System.err.println("Parsing background: "+textCell+" is not a number.");
                    }
                    currCol ++;
                }
            }
            currRow ++;
        }
        
        rows = currRow;
        cols = currCol;
    }
    
    /**
     * This will read a file which describes the layout of the foreground
     * objects, it will then add those tiles to layer 1 of the display list
     * @param path
     */
    public void setForeground(String path){
        System.out.println("reading foreground: "+path);
        //0 for nothing
        //1 for fence
        //2 for enemies
        //3 for player
        
        ArrayList<ArrayList<Boolean>> isFence = new ArrayList<ArrayList<Boolean>>();
        walls = new boolean[rows][cols];
        
        LinkedList<String> cells = parseFile(path);
        int currRow = 0;
        for (String textRow : cells){
            String[] textCells = textRow.split(" ");
            int currCol = 0;
            isFence.add(new ArrayList<Boolean>());
            for(String textCell : textCells){
                if(!textCell.equals("")){
                    try{
                        int cell = Integer.parseInt(textCell);
                        
                        if(cell == 1){
                            isFence.get(currRow).add(true);
                            walls[currRow][currCol] = true;
                        }else{
                            isFence.get(currRow).add(false);
                            walls[currRow][currCol] = false;
                            if(cell == 2){
                                Tile enemy = new Tile("images/Chupacabra",true);
                                enemy.swapAndResume("ChupaIdleFront");
                                enemy.dir = Tile.Direction.DOWN;
                                Point position = tileToXY(currCol,currRow);
                                enemy.x = position.x;
                                enemy.y = position.y;
                                enemy.row = currRow;
                                enemy.col = currCol;
                                model.addChild(enemy, 2);
                                model.enemies.add(enemy);
                            }else if(cell == 3){
                                model.player = new Tile("images/player",true);
                                model.player.swapAndResume("HershelFrontIdle");
                                model.player.dir = Tile.Direction.DOWN;
                                Point position = tileToXY(currCol,currRow);
                                model.player.x = position.x;
                                model.player.y = position.y;
                                model.player.row = currRow;
                                model.player.col = currCol;
                                model.addChild(model.player, 2);
                                
                            }
                            else if(cell == 4){
                                Tile t = new Tile("images/scenery/barrel4.png");
                                Point position = tileToXY(currCol,currRow);
                                t.x = position.x;
                                t.y = position.y;
                                t.row = currRow;
                                t.col = currCol;
                                model.addChild(t, 2);
                                walls[currRow][currCol] = true;
                                
                            }
                            else if(cell == 5){
                                Tile t = new Tile("images/scenery/corn2.png");
                                Point position = tileToXY(currCol,currRow);
                                t.x = position.x;
                                t.y = position.y;
                                t.setOrigin(0, 100); //tile is taller than usual
                                t.row = currRow;
                                t.col = currCol;
                                model.addChild(t, 2);
                                walls[currRow][currCol] = true;
                                
                            }
                            else if(cell == 6){
                                Tile t = new Tile("images/scenery/scarecrow.png");
                                Point position = tileToXY(currCol,currRow);
                                t.x = position.x;
                                t.y = position.y;
                                t.setOrigin(0, 100); //tile is taller than usual
                                t.row = currRow;
                                t.col = currCol;
                                model.addChild(t, 2);
                                walls[currRow][currCol] = true;
                                
                            }
                            else if(cell == 7){
                                Tile t = new Tile("images/scenery/Chicken.png");
                                Point position = tileToXY(currCol,currRow);
                                t.x = position.x;
                                t.y = position.y;
                                t.row = currRow;
                                t.col = currCol;
                                model.addChild(t, 2);
                                walls[currRow][currCol] = true;
                                
                            }
                            else if(cell == 8){
                                Tile t = new Tile("images/scenery/Pig.png");
                                Point position = tileToXY(currCol,currRow);
                                t.x = position.x;
                                t.y = position.y;
                                t.row = currRow;
                                t.col = currCol;
                                model.addChild(t, 2);
                                walls[currRow][currCol] = true;
                                
                            }
                            else if(cell == 9){
                                Tile t = new Tile("images/scenery/Sheep.png");
                                Point position = tileToXY(currCol,currRow);
                                t.x = position.x;
                                t.y = position.y;
                                t.row = currRow;
                                t.col = currCol;
                                model.addChild(t, 2);
                                walls[currRow][currCol] = true;
                                
                            }
                            else if(cell == 10){
                                model.battery = new Tile("images/battery");
                                Point position = tileToXY(currCol,currRow);
                                model.battery.x = position.x;
                                model.battery.y = position.y;
                                model.battery.row = currRow;
                                model.battery.col = currCol;
                                model.addChild(model.battery, 2);
                                
                            }
                        }
                        
                    }catch(NumberFormatException e){
                        System.err.println("Parsing background: "+textCell+" is not a number.");
                    }
                    currCol ++;
                }
            }
            currRow ++;
        }
        
        //handle fences
        for(int r=rows-1;r>=0;r--){
            for(int c=0;c<cols;c++){
                if(isFence.get(r).get(c)){
                    String im = getFencePiece(isFence,r,c);
                    
                    Tile fenceMC = new Tile(im);
                    Point position = tileToXY(c,r);
                    fenceMC.x = position.x;
                    fenceMC.y = position.y;
                    fenceMC.row = r;
                    fenceMC.col = c;
                    model.addChild(fenceMC, 2);
                }
            }
        }
        sortDepth(2);
    }
    
    

    private boolean getBoolCell(ArrayList<ArrayList<Boolean>> isFence, int r, int c) {
        if(!inBounds(r,c)){
            return false;
        }else{
            return isFence.get(r).get(c);
        }
    }
    
    public boolean inBounds (int r, int c){
        return !(r<0 || c<0 || r>= rows || c >= cols);
    }

    /**
     * This will ensure everything is sorted correctly by depth
     * make sure to implement Comparable in Tile class
     */
    public void sortDepth(int layer){
        ArrayList<MovieClip> tiles = model.displayList.get(layer);
        //Use insertion sort for mostly sorted list
        //alter compareTo to determine if data should be sorted by row AND col
        for(int i=1;i<tiles.size();i++){
            Tile valueToInsert = (Tile) tiles.get(i);
            int holePos = i;
            Tile previous = (Tile) tiles.get(holePos - 1);
            while (holePos > 0 && valueToInsert.compareDepth(previous) == -1){
                tiles.set(holePos, previous);
                holePos -= 1;
                if(holePos>0) {
                    //loop will terminate on next iteration so we
                    //no longer need previous
                    previous = (Tile) tiles.get(holePos - 1);
                }
            }
            tiles.set(holePos, valueToInsert);
        }
    }
    
    
    
    /**
     * This will convert tile coordinates to screen coordinates
     * @param tileX
     * @param tileY
     * @return A point of x,y
     */
    public Point tileToXY (int tileX, int tileY){
        int x = tileX*width + tileY*skewX;
        int y = tileY*height + tileX*skewY;
        return new Point(x,y);
    }
    
    /**
     * This will convert tile coordinates to screen coordinates
     * @param tileX
     * @param tileY
     * @return A double-point of x,y
     */
    public DPoint tileToXY (double tileX, double tileY){
        double x = tileX*width + tileY*skewX;
        double y = tileY*height + tileX*skewY;
        return new DPoint(x,y);
    }
    
    /**
     * This will convert screen coordinates to tile coordinates
     * @param x
     * @param y
     * @return A double-point of tileX,tileY
     */
    public DPoint xyToTile(double x, double y){
        double denom = width*height - skewY*skewX;
        double tileX = (x*height-y*skewX)/denom;
        double tileY = (y*width-x*skewY)/denom;
        return new DPoint(tileX,tileY);
    }
    
    /**
     * This will convert screen coordinates to tile coordinates, but
     * will take into account where the top left point of the tile
     * actually is within the picture. So it is easier to use this
     * to locate which tile is under a given point
     * @param vx 
     * @param vy
     * @return A double-point of tileX, tileY
     */
    public DPoint visualXYToTile(int vx, int vy){
        double x = vx - visualX;
        double y = vy - visualY;
        return xyToTile(x,y);
    }
    
    public Point tileToVisualXY (double tileX, double tileY){
        int x = (int) (tileX*width + tileY*skewX + visualX);
        int y = (int) (tileY*height + tileX*skewY + visualY);
        return new Point(x,y);
    }
    
    
    public void realPosition(Tile t){
        Point pos = tileToXY(t.col, t.row);
        t.x = pos.x;
        t.y = pos.y;
    }
    
    private LinkedList<String> parseFile(String path){
        /*
         *for files not packaged in jar, path should not have a leading /
        LinkedList<String> result = new LinkedList<String>();
        BufferedReader br = null;
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(path));
            while ((sCurrentLine = br.readLine()) != null) {
                result.add(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
        */
        //for files packaged into jar, path should have a leading /
        URL url = this.getClass().getResource(path);
        LinkedList<String> result = new LinkedList<String>();
        BufferedReader br = null;
        try {
            String sCurrentLine;
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((sCurrentLine = br.readLine()) != null) {
                result.add(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    private String getFencePiece(ArrayList<ArrayList<Boolean>> isFence, int r,
            int c) {
        boolean top = getBoolCell(isFence, r-1, c);
        boolean bottom = getBoolCell(isFence, r+1, c);
        boolean left = getBoolCell(isFence, r, c-1);
        boolean right = getBoolCell(isFence, r, c+1);

        //lb corn = 2 
        if(left && bottom){
            return "images/fences/fence0002.png";
        }
        //lr = 4
        if(left && right) {
            return "images/fences/fence0004.png";
        }
        //lt corn = 5
        if (left && top){
            return "images/fences/fence0005.png";
        }
        //rb corn = 6
        if (right && bottom){
            return "images/fences/fence0006.png";
        }
        //rt corn = 8
        if (right && top){
            return "images/fences/fence0008.png";
        }
        //tb = 9
        if (top && bottom){
            return "images/fences/fence0009.png";
        }
        //r end = 7
        if (right){
            return "images/fences/fence0007.png";
        }
        //l end = 3
        if (left){
            return "images/fences/fence0003.png";
        }
        //t end = 10
        if (top){
            return "images/fences/fence0010.png";
        }
        //b end = 1
        if (bottom){
            return "images/fences/fence0001.png";
        }
        System.err.println("No available fence");
        return "images/fences/fence0010.png";
    }

    public void calculateBits(Tile character, Point newPosition) {
        int x1 = character.col;
        int y1 = character.row;
        int x2 = newPosition.x;
        int y2 = newPosition.y;
        Point bits = tileToXY(x2-x1,y2-y1);
        character.bitX = (double) bits.x / Constants.MOVE_TIME;
        character.bitY = (double) bits.y / Constants.MOVE_TIME;
    }
    
    public boolean[][] wallsAndEnemies (){
        boolean[][] result = new boolean[rows][cols];
        for(int i =0;i<walls.length;i++){
            //clone only does a shallow copy, so need
            //a for loop too
            result[i] = walls[i].clone();
        }
        
        for(Tile enemy : model.enemies){
            result[enemy.row][enemy.col] = true;
        }
        return result;
    }
}
