package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;

import controller.advancedtools.Derivative;
import model.Constants;

/**
 * This is the key listener that gets added to the content pane of the
 * main GUI. This holds an internal array of which keys are pressed down
 * at any given moment. And they can be retrieved with the isDown function.
 * Also in this class are functions to get directional vectors based on
 * ASWD or arrow key movement. Users should not use the keyPressed function
 * but instead access isDown during the time step
 * @author Nick Cheng
 */
public class KeyHandler implements KeyListener {
    Controller controller;
    
    private boolean[] keysDown; 
    private Derivative[] keyDerivs;
    private int[] keyDerivResults;
    
    public KeyHandler(Controller controller){
        if(Constants.DEBUG) System.out.println("key handler init, ready.");
        this.controller = controller;
        keysDown = new boolean[128];
        keyDerivs = new Derivative[128];
        keyDerivResults = new int[128];
        
        for(int i=0;i<128;i++){
            keyDerivs[i] = new Derivative();
        }
    }

    @Override
    public void keyPressed(KeyEvent k) {
        //System.out.println(k.getKeyCode());
        if(k.getKeyCode()>=128) return;
        keysDown[k.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent k) {
        if(k.getKeyCode()>=128) return;
        keysDown[k.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent k) {}
    
    /**
     * Returns whether or not the key with the given code is
     * pressed down.
     * @param code The ASCII key code for a certain button
     * @return true iff the key is down
     */
    public boolean isDown(int code){
        return keysDown[code];
    }
    
    /**
     * This updates the Derivatives so that keyJustPressed and
     * keyJustReleased return the correct results. This function
     * is called at a fixed rate alongside the game loop.
     */
    public void updateKeyPresses(){
        for(int i =0;i<128;i++){
            keyDerivResults[i] = keyDerivs[i].getDelta(isDown(i));
        }
    }
    
    /**
     * This returns if the key is pressed down, but was not
     * pressed down during the last time step.
     * @param code The ASCII key code for a certain button
     * @return true iff the key was just pressed
     */
    public boolean keyJustPressed(int code){
        return keyDerivResults[code] == 1;
    }
    
    /**
     * This returns if the key is not pressed down, but was
     * pressed down during the last time step.
     * @param code The ASCII key code for a certain button
     * @return true iff the key was just pressed
     */
    public boolean keyJustReleased(int code){
        return keyDerivResults[code] == -1;
    }
    
    /**
     * Returns a unit vector for movement based on the ASWD keys.
     * Note: when diagonal, the magnitude is still 1.0 (0.707,0.707)
     * @return vector in the form of a Point
     */
    public Point2D.Double aswdVector () {
        double x = 0.0;
        double y = 0.0;
        if(isDown(65)){
            x -= 1.0;
        }
        if(isDown(68)){
            x += 1.0;
        }
        if(isDown(87)){
            y -= 1.0;
        }
        if(isDown(83)){
            y += 1.0;
        }
        double mag = Math.sqrt(x*x + y*y);
        if(mag == 0.0){
            return (new Point2D.Double(0.0,0.0));
        }else{
            return (new Point2D.Double(x/mag,y/mag));
        }
    }
    
    /**
     * Returns a unit vector for movement based on the arrow keys.
     * Note: when diagonal, the magnitude is still 1.0 (0.707,0.707)
     * @return vector in the form of a Point
     */
    public Point2D.Double arrowVector () {
        double x = 0.0;
        double y = 0.0;
        if(isDown(37)){
            x -= 1.0;
        }
        if(isDown(39)){
            x += 1.0;
        }
        if(isDown(38)){
            y -= 1.0;
        }
        if(isDown(40)){
            y += 1.0;
        }
        double mag = Math.sqrt(x*x + y*y);
        if(mag == 0.0){
            return (new Point2D.Double(0.0,0.0));
        }else{
            return (new Point2D.Double(x/mag,y/mag));
        }
    }
}
