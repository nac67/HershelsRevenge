package view;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import model.Model;
import model.Constants;
import controller.Controller;


/**
 * This is the view in the model-view-controller pattern.
 * 
 * The view creates the JFrame and all of the components.
 * It also adds appropriate listeners to the components
 * which the controller will handle. It contains a reference
 * to the model, so that it can graphically update in accordance
 * to the model.
 * @author Nick Cheng
 */
public class View {    
    private JFrame frame;
    private JPanel content;
    private GameArea game;
    
    Cursor blankCursor; //for use in hiding mouse
    
    /** Constructor */
    public View(Controller controller, Model model) {
        if(Constants.DEBUG) System.out.println("view init, ready.");
        
        //initialize frame
        frame = new JFrame();
        frame.setTitle("Chupa Chomp");
        frame.setResizable(false);
        frame.setLocationByPlatform(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //initialize content panel
        content = (JPanel) frame.getContentPane();
        content.setBackground(Color.white);
        content.setFocusable(true);
        content.addKeyListener(controller.gameKeys);
        
        //initialize game area
        game = new GameArea(model,Constants.GAME_WIDTH,Constants.GAME_HEIGHT);
        game.addMouseListener(controller.gameMouse);
        content.add(game);
        
        //size the jframe appropriately
        frame.pack();
        Insets insets = frame.getInsets();
        int extraX = insets.left + insets.right;
        int extraY = insets.top + insets.bottom;
        frame.setSize(Constants.GAME_WIDTH+extraX, Constants.GAME_HEIGHT+extraY);
        
        //make visual component appear
        frame.setVisible(true); 

        // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        // Create a new blank cursor.
        blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        
    }
    
    /**
     * This is the main graphical update method in the program.
     * It takes in a model which holds the state and prepares the
     * components to reflect the state of the program then calls
     * repaint.
     */
    public void updateGraphics (){
        game.repaint();
    }
    
    /**
     * This gives the relative position of the mouse to the game area
     * @return a Point that represents the mouse
     */
    public Point getMouse(){
        Point pos = game.getMousePosition();
        if (pos == null){
            return new Point (-100,-100);
        }else{
            return pos;
        }
    }

    public void hideMouse(){
        content.setCursor(blankCursor);

    }
    public void showMouse(){
        content.setCursor(null);
    }
}
