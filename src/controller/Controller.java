package controller;

import model.Constants;
import model.Model;
import model.utils.JarCalc;
import model.utils.Utils;
import view.View;

/**
 * This is the controller in the model-view-controller pattern.
 * 
 * I opted for a passive version of the MVC pattern.
 * This means it is up to the controller to update the view instead
 * of the model. The controller is created by the main method
 * and then creates instances of the model and the view. The controller
 * will apply logic to make changes to the model, and after changes are
 * made it will call upon the view to update the GUI in accordance to
 * the updated model.
 * @author Nick Cheng
 */
public class Controller {    
    /** the view handles the GUI and adds
     *  appropriate event listeners */
    private View view;
    
    /** the model handles state and data 
     *  structures of the game */
    public Model model;
    
    /** the mouse handler is a listener for 
     *  the mouse events in the frame */
    public AbstractMouseHandler gameMouse;
    
    /** the frame handler runs at a fixed rate 
     *  and contains the main game loop */
    public FrameHandler gameFrames;
    
    /** the key handler listens for key strokes 
     *  and holds a list of currently pressed keys */
    public KeyHandler gameKeys;
    

    /** Constructor */
    public Controller() {
        Utils.cd = JarCalc.getJarDir(this.getClass());
        System.out.println(Utils.cd);
        //initialize model, view, key and mouse
        model = new Model(this);
        gameKeys = new KeyHandler(this);
        gameMouse = new MouseHandler(this, model);
        view = new View(this, model);
        gameMouse.setView(view);
        model.startGame();
        
        //initialize game loop
        gameFrames =  new FrameHandler(this, model);
        new javax.swing.Timer(Constants.TIME_STEP, gameFrames).start();
        
        if(Constants.DEBUG) System.out.println("controller finished tasks.");
        notifyView();
    }

    /**
     * This function makes graphical updates to the GUI
     */
    public void notifyView(){
        view.updateGraphics();
    }
    
    public static void main(String[] args) {
        new Controller();
    }
}
