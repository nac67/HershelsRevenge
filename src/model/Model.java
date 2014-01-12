package model;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;

import view.NoSaveException;
import view.SaveFile;
import view.SoundEffect;
import model.animation.Animation;
import model.tileboard.GameTiles;
import controller.Controller;

/**
 * This is the model in the model-view-controller pattern.
 * 
 * It handles the internal representation of the data.
 * For the user: declare any movie clips or other data structures
 * you want to here, then you can write startGame which can 
 * actually instantiate the movie clips and add them to the
 * display list.
 * @author Nick Cheng
 */
public class Model {
    
    public String[] levelForegrounds = {
            "levels/tutorial/fg.txt",
            //"levels/level1fg.txt",
            "levels/lure/level1fg.txt",
            "levels/U/level1fg.txt",
            "levels/foursquare/level1fg.txt",
            "levels/cornmaze/level1fg.txt",
            "levels/trapped2/level1fg.txt"
    };
    public String[] levelBackgrounds = {
            "levels/tutorial/bg.txt",
            //"levels/level1bg.txt",
            "levels/lure/level1bg.txt",
            "levels/U/level1bg.txt",
            "levels/foursquare/level1bg.txt",
            "levels/cornmaze/level1bg.txt",
            "levels/trapped2/level1bg.txt"
    };
    
    public int levelsCompleted = 0;
    public int totalLevels = 6;
    public int currentLevel;
    
    
    public Color bgColor = new Color (13, 62, 59);
    
    public boolean useMouse = false;

    public Point lastMouse;
    
    public GameTiles gameboard;
    public enum State {
        MAIN_MENU, INSTRUCTIONS, LEVEL_SELECT, WAIT_FOR_PLAYER,
        MOVE_CHARACTERS, GAME_OVER, PAUSED;
    }
    public State gameState;
    public State savedState; //for unpausing
    public int moveTimer;
    public int levelTimer;
    
    public Tile player;
    public int lastDir; //inv: cannot be -1
    public boolean playerHasTurned;
    
    public ArrayList<Point> currLight;
    public ArrayList<Point> lastLight;
    public Area flashlight;
    
    public ArrayList<Tile> enemies;
    public Tile gameOverEnemy;
    
    public int dieTime;
    
    //list of ducking animations so that they can be deleted if light 
    //switches off of them before they are finished ducking
    public ArrayList<Tile> duckingEnemies; 
    
    public SoundEffect stepSound;
    
    public boolean menuReady;
    public ArrayList<MovieClip> menuRemovables;
    public ArrayList<MovieClip> menuLevelTicks;
    public MovieClip select_btn;
    public MovieClip instructions_btn;
    public MovieClip continue_btn;
    public MovieClip black; //blackness behind menus
    
    
    
    public void startGame(){
        //preload images
        new MovieClip("images/player",true);
        new MovieClip("images/Chupacabra",true);
        
        //load sounds
        stepSound = new SoundEffect("sounds/brush3.wav");
        
        menuRemovables = new ArrayList<MovieClip>();
        menuReady = false;
        gameState = State.MAIN_MENU;
        //load player save
        if(Constants.DO_SAVES){
            try {
                levelsCompleted = SaveFile.loadGame();
                if(Constants.DEBUG) System.out.println("Loading of Saved Game Successful");
            } catch (NoSaveException e) {
                levelsCompleted = 0;
                if(Constants.DEBUG) System.out.println("Loading of Saved Game Failure");
            }
        }
        
    }
    
    private void loadLevelEssentials(){
        clearMenus();
        removeAllChildren();
        gameState = State.WAIT_FOR_PLAYER;
        enemies = new ArrayList<Tile>();
        duckingEnemies = new ArrayList<Tile>();
        lastDir = 1;
        playerHasTurned = false;
        
        gameboard = new GameTiles(this,72,64,-28,0,28,36);
        
        levelShiftX = 150;
        levelShiftY = 0;
        moveTimer = 0;

        lastLight = new ArrayList<Point>();
        currLight = new ArrayList<Point>();
        
        lastMouse = new Point();
        
        
        SoundEffect.muted = true;
        
    }
    
    public void loadLevel(int n){
        if(Constants.DEBUG)
            System.out.println("===========LOADING LEVEL "+n);
        loadLevelEssentials();
        gameboard.setBackground(levelBackgrounds[n]);
        gameboard.setForeground(levelForegrounds[n]);
        centerOnPlayer();
        levelTimer = 0;
    }
    
    
    
    public void drawMainMenu() {
        clearMenus();
        
        MovieClip titlebg = new MovieClip("images/ui/title/titlebg.png");
        titlebg.x = -levelShiftX;
        titlebg.y = -levelShiftY;
        addChild(titlebg,0);
        
        MovieClip logo = new MovieClip("images/ui/title/logo.png");
        addChild(logo,1);
        menuRemovables.add(logo);
        logo.x = 200-levelShiftX;
        logo.y = 50-levelShiftY;
        
        select_btn = new MovieClip("images/ui/buttons/select/");
        select_btn.gotoAndStop(1);
        addChild(select_btn,3);
        menuRemovables.add(select_btn);
        select_btn.x = 300-levelShiftX;
        select_btn.y = 300-levelShiftY;
        
        instructions_btn = new MovieClip("images/ui/buttons/instructions/");
        instructions_btn.gotoAndStop(1);
        addChild(instructions_btn,3);
        menuRemovables.add(instructions_btn);
        instructions_btn.x = 300-levelShiftX;
        instructions_btn.y = 360-levelShiftY;
    }
    
    public void drawInstructions() {
        clearMenus();
        MovieClip inst = new MovieClip("images/ui/title/instructions overlay.png");
        inst.x = -levelShiftX;
        inst.y = -levelShiftY;
        addChild(inst,3);
        menuRemovables.add(inst);
        
        select_btn = new MovieClip("images/ui/buttons/select/");
        select_btn.gotoAndStop(1);
        addChild(select_btn,3);
        menuRemovables.add(select_btn);
        select_btn.x = 300-levelShiftX;
        select_btn.y = 500-levelShiftY;
    }
    
    public void drawLevelSelect() {
        clearMenus();
        menuLevelTicks = new ArrayList<MovieClip>();
        for(int i=0;i<totalLevels;i++){
            MovieClip tick = new MovieClip("images/ui/level icons");
            addChild(tick,3);
            menuRemovables.add(tick);
            tick.x = (i%5)*60 + 250-levelShiftX;
            tick.y = (i/5)*60 + 150-levelShiftY;
            
            if(i < levelsCompleted){
                tick.gotoAndStop(i+20);
                tick.bonusVariable = i;
                menuLevelTicks.add(tick);
            }else if(i == levelsCompleted){
                tick.gotoAndStop(i);
                tick.bonusVariable = i;
                menuLevelTicks.add(tick);
            }else{
                tick.gotoAndStop(40);
            }
        }
        
        if(levelsCompleted == totalLevels){
            MovieClip yay = new MovieClip("images/ui/title/congrats.png");
            yay.gotoAndStop(1);
            addChild(yay,3);
            menuRemovables.add(yay);
            yay.x = 140-levelShiftX;
            yay.y = 100-levelShiftY;
        }
        
        instructions_btn = new MovieClip("images/ui/buttons/instructions/");
        instructions_btn.gotoAndStop(1);
        addChild(instructions_btn,3);
        menuRemovables.add(instructions_btn);
        instructions_btn.x = 300-levelShiftX;
        instructions_btn.y = 500-levelShiftY;
    }
    
    public void drawPauseMenu() {
        clearMenus();
        
        black = blackness();
        addChild(black,3);
        
        select_btn = new MovieClip("images/ui/buttons/select/");
        select_btn.gotoAndStop(1);
        addChild(select_btn,3);
        menuRemovables.add(select_btn);
        select_btn.x = 300-levelShiftX;
        select_btn.y = 300-levelShiftY;
        
        continue_btn = new MovieClip("images/ui/buttons/continue/");
        continue_btn.gotoAndStop(1);
        addChild(continue_btn,3);
        menuRemovables.add(continue_btn);
        continue_btn.x = 300-levelShiftX;
        continue_btn.y = 250-levelShiftY;
    }
    
    public void clearMenus(){
        for(MovieClip item : menuRemovables){
            removeChild(item);
        }
        menuRemovables.clear();
    }
    
    
    public void completeLevel(){
        MovieClip black = blackness();
        addChild(black,2);
        
        gameState = State.LEVEL_SELECT;
        if(currentLevel+1 > levelsCompleted){
            levelsCompleted = currentLevel+1;
            if(Constants.DO_SAVES){
                SaveFile.saveGame(levelsCompleted);
                if(Constants.DEBUG) System.out.println("Saved Game Successfully");
            }
        }
    }
    
    public MovieClip blackness () {
        MovieClip black = new MovieClip("images/ui/black50.png");
        black.scaleX = Constants.GAME_WIDTH/100;
        black.scaleY = Constants.GAME_HEIGHT/100;
        black.x = -levelShiftX;
        black.y = -levelShiftY;
        return black;
    }
    
    public void centerOnPlayer(){
        double targetX =(player.x )+gameboard.visualX+gameboard.width/2;
        double targetY = (player.y)+gameboard.visualY+gameboard.height/2;
        
        levelShiftX = (int) (-targetX + Constants.GAME_WIDTH/2);
        levelShiftY = (int) (-targetY + Constants.GAME_HEIGHT/2);
    }
    ///////////////////////////////////
    // END USER EDITED SECTION
    ///////////////////////////////////
    
    /** 
     * Each display layer contains movie clips that are drawn 
     * to the frame sequentially during every time step.
     * If a movie clip is earlier in the list, it will be 
     * drawn earlier and therefore be "behind" other movie clips.
     * 
     * This list contains DISPLAY_LAYERS amount of display lists
     * numbered: 0 to DISPLAY_LAYERS (inclusive)
     * Each layer will be drawn sequentially so that lower
     * number layers will be "behind" other layers.
     * 
     * In summary, each layer has its own ordering, but,
     * for example, everything on layer 1 will be above 
     * everything on layer 0.
     * 
     * If the user does not care about layers, they could simply
     * call addChild without a layer number and everything will
     * be defaulted to layer 0.
     */
    public ArrayList<ArrayList<MovieClip>> displayList;
    
    /**
     * These movie clips will be removed on the next time step instead
     * of immediately. Currently the only use for this is for movie clips
     * that delete themselves at the end of playback, since otherwise
     * they would be trying to delete themselves while the iterator
     * is going through the list
     */
    public LinkedList<MovieClip>removeQueue;
    
    public Controller controller;
    
    public int levelShiftX = 0;
    public int levelShiftY = 0;


    
    
    
    
    /** Constructor */
    public Model (Controller controller){
        if(Constants.DEBUG) System.out.println("model init, ready.");
        Animation.loadedAnimations = new Hashtable<String,ArrayList<BufferedImage>>();
        displayList = new ArrayList<ArrayList<MovieClip>>();
        for (int i=0;i<Constants.NUM_DISPLAY_LAYERS;i++){
            displayList.add(new ArrayList<MovieClip>());
        }
        removeQueue = new LinkedList<MovieClip>();
        this.controller = controller;
        
        flashlight = new Area();
    }
    
   
    
    /**
     * This will add a movie clip to layer 0 of the
     * display list
     * @param mc The movie clip to add
     */
    public void addChild(MovieClip mc){
        addChild(mc,0);
    }
    
    /**
     * This will add a movie clip to a given layer of
     * the display list. Layers are numbered  0 to DISPLAY_LAYERS (inclusive)
     * @param mc The movie clip to add
     * @param layer The layer to add it to
     */
    public void addChild(MovieClip mc, int layer){
        if(layer>=Constants.NUM_DISPLAY_LAYERS){
            System.err.println("Layer is out of range");
        }
        displayList.get(layer).add(mc);
        mc.parent = this;
    }
    
    /**
     * This returns the number of the layer containing
     * a given movie clip
     * @param mc The movie clip to search for
     * @return The layer that contains the movie clip
     */
    public int layerContaining(MovieClip mc){
        int layer = -1;
        for (int i=0;i<Constants.NUM_DISPLAY_LAYERS;i++){
            if(displayList.get(i).contains(mc)){
                layer = i;
            }
        }
        if (layer == -1){
            System.err.println("could not find movie clip");
        }
        return layer;
    }
    
    /**
     * This will move a movie clip to the top of a different layer
     * @param mc The movie clip to move
     * @param layer The layer to move it to
     */
    public void moveLayer(MovieClip mc, int layer){
        int oldLayer = layerContaining(mc);
        displayList.get(oldLayer).remove(mc);
        displayList.get(layer).add(mc);
    }
    
    /**
     * This will remove a movie clip from the display list
     * so that it no longer appears visible. It can be re-added later
     * @param mc The movie clip to remove
     */
    public void removeChild(MovieClip mc){
        int layer = layerContaining(mc);
        displayList.get(layer).remove(mc);
    }
    
    /**
     * This will remove a movie clip on the next time step
     * @param mc The movie clip to remove
     */
    public void removeNextTime(MovieClip mc){
        removeQueue.add(mc);
    }
    
    /**
     * This will remove all movie clips from all layers of
     * the display list
     */
    public void removeAllChildren(){
        for (int i=0;i<Constants.NUM_DISPLAY_LAYERS;i++){
            displayList.get(i).clear();
        }
    }
    
    /**
     * This will remove all movie clips from all layers of
     * the display list on a given layer
     * @param layer The layer to remove all children from
     */
    public void removeAllChildrenOnLayer(int layer){
        displayList.get(layer).clear();
    }
    
    /**
     * This will swap the depths of two movie clips. These
     * movie clips can be on the same layer, and they will
     * switch depths, or they can be on different layers in
     * which case the depth within each layer will be preserved.
     * CAUTION: Linear time
     * @param mc1 The first movie clip
     * @param mc2 The movie clip to switch it with
     */
    public void swapChildren(MovieClip mc1, MovieClip mc2){
        int l1 = layerContaining(mc1);
        int l2 = layerContaining(mc2);
        int p1 = displayList.get(l1).indexOf(mc1);
        int p2 = displayList.get(l2).indexOf(mc2);
        
        if(l1 == l2){
            //If on same layer
            Collections.swap(displayList.get(l1), p1, p2);
        }else{
            //If on different layers
            displayList.get(l1).remove(mc1);
            displayList.get(l2).remove(mc2);
            displayList.get(l2).add(p2, mc1);
            displayList.get(l1).add(p1, mc2);
        }
        
    }
    
    /**
     * This will bring a movie clip to the front of its layer
     * @param mc1 The movie clip to move
     */
    public void bringToFront(MovieClip mc1){
        int layer = layerContaining(mc1);
        int p1 = displayList.get(layer).indexOf(mc1);
        Collections.swap(displayList.get(layer), p1, displayList.get(layer).size()-1);
    }
    
    /**
     * This will send a movie clip to the back of its layer
     * @param mc1
     */
    public void sendToBack(MovieClip mc1){
        int layer = layerContaining(mc1);
        int p1 = displayList.get(layer).indexOf(mc1);
        Collections.swap(displayList.get(layer), p1, 0);
    }

    

    
}
