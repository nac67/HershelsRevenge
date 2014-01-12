package model;

public class Constants {

    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;
    
    /**
     * Number of milliseconds between each time-step
     * Try to keep it a multiple of 1/60th of a second
     * to match screen refresh rate.
     * 
     * Use 33 for 30fps
     * Use 16 for 60fps
     */
    public static final int TIME_STEP = 33;
    
    /**
     * Number of layers to contain movie clips.
     * Layers will be numbered 0 to DISPLAY_LAYERS (inclusive)
     * Each layer can hold as many movie clips as it needs to.
     * see displayList in model for more details
     */
    public static final int NUM_DISPLAY_LAYERS = 4;
    
    /**
     * Display debugging terminal messages
     */
    public static final boolean DEBUG = true;
    
    /**
     * Time spent for characters to move between cells
     */
    public static final int MOVE_TIME = 8;
    
    public static final int DIE_TIME = 60;
    public static final int DIE_TIME2 = 90;
    
    /**
     * Length of beam of flashlight
     */
    public static final int BEAM_LENGTH = 5;
    
    public static final int MOUSE_EDGE_LOOK_PADDING = 100;
    
    public static final int KEYBOARD_LOOK_REACH = 200;
    
    public static final boolean DO_SAVES = true;

}
