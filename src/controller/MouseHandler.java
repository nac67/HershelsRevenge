package controller;

import java.awt.event.MouseEvent;

import model.Model;

/**
 * This is the mouse handler to be edited by the user.
 * Make sure to call the super methods.
 * 
 * This also inherits controller and view from AbstractMouseHandler
 * @author Nick Cheng
 *
 */
public class MouseHandler extends AbstractMouseHandler {

    //model variable inherited
    
    public MouseHandler(Controller controller, Model model) {
        super(controller, model);
    }
    
    ///////////////////////////////////
    // XXX: BEGIN USER EDITED SECTION
    ///////////////////////////////////
    
    //override any other mouse functions you want
    //just be sure to call the super mouse function
    
    @Override
    public void mousePressed(MouseEvent m) {
        synchronized(model){
            super.mousePressed(m);
//            int realX = m.getX() - model.levelShiftX;
//            int realY = m.getY() - model.levelShiftY;
//            DPoint coor = model.gameboard.visualXYToTile(realX, realY);
            //System.out.println(coor);
            //System.out.println("("+Math.floor(coor.x)+", "+Math.floor(coor.y)+")");
        }
    }
}
