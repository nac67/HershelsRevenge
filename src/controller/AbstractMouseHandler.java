package controller;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import controller.advancedtools.Derivative;
import model.Constants;
import model.Model;
import view.View;

/**
 * This is the listener that gets added to the game
 * area in the view. It handles all mouse events and
 * applies logic to make changes to the model.
 * This is abstract and supposed to remain unchanged.
 * For changes, edit the MouseHandler, but be sure
 * to call the super methods.
 * @author Nick Cheng
 */
public abstract class AbstractMouseHandler implements MouseListener {
    Controller controller;
    View view;
    Model model;
    
    public boolean leftButton = false;
    public boolean rightButton = false;
    
    private Derivative leftDir;
    public boolean leftJustPressed;
    
    public AbstractMouseHandler (Controller controller, Model model){
        if(Constants.DEBUG) System.out.println("mouse handler init, waiting on view.");
        this.controller = controller;
        this.model = model;
        leftDir = new Derivative();
    }
    
    public void setView (View view){
        if(Constants.DEBUG) System.out.println("mouse handler set view, ready.");
        this.view = view;
    }
    
    @Override
    public void mouseClicked(MouseEvent m) {
    }

    @Override
    public void mouseEntered(MouseEvent m) {
    }

    @Override
    public void mouseExited(MouseEvent m) {
    }

    @Override
    public void mousePressed(MouseEvent m) {
        if (m.getButton() == MouseEvent.BUTTON1){
            leftButton = true;
        }
        if (m.getButton() == MouseEvent.BUTTON3){
            rightButton = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent m) {
        if (m.getButton() == MouseEvent.BUTTON1){
            leftButton = false;
        }
        if (m.getButton() == MouseEvent.BUTTON3){
            rightButton = false;
        }
    }
    
    public boolean leftDown () {
        return leftButton;
    }
    
    public boolean rightDown () {
        return rightButton;
    }
    
    public int mouseX () {
        try{
            return view.getMouse().x;
        }catch(Exception e){
            return 0;
        }
        
    }
    
    public int mouseY () {
        try{
            return view.getMouse().y;
        }catch(Exception e){
            return 0;
        }
    }
    
    public boolean isOnScreen () {
        try{
            int x = view.getMouse().x;
            int y = view.getMouse().y;
            if (x >= 0 && y >= 0 && x < Constants.GAME_WIDTH && y < Constants.GAME_HEIGHT){
                return true;
            }
            return false;
        }catch(Exception e){
            return false;
        }
    }
    
    public void hideMouse(){
        view.hideMouse();
    }
    
    public void showMouse(){
        view.showMouse();
    }
    
    public Point mouseLoc () {
        return view.getMouse();
    }
    
    public Point mouseLocLocal(){
        Point p = mouseLoc();
        p.x -= model.levelShiftX;
        p.y -= model.levelShiftY;
        return p;
    }
    
    public void updateDeriv () {
        leftJustPressed = leftDir.getDelta(leftButton) == 1;
    }
    
}