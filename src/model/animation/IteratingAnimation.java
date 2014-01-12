package model.animation;

import java.awt.image.BufferedImage;

/**
 * This interface describes any type of class that is able
 * to iterate through frames while returning a new image for
 * use with animations that are multiple frames.
 * 
 * There is also the notion of direction which describes in which
 * order the animation will play
 * @author Nick Cheng
 */
public interface IteratingAnimation {
    
    public enum Direction {
        FORWARD(1), BACKWARD(-1), PAUSED(0);
        
        public int speed;
        
        private Direction(int s) {
            speed = s;
        }
    }
    
    /**
     * This will determine which the direction the 
     * animation will go in when getNextFrame is called
     * @param dir Direction.Forward, Direction.Backward, Direction.PAUSED
     */
    void setDirection(Direction dir);
    
    /**
     * This will increment the animation to the next frame
     * and return the buffered image of that frame
     * @return A buffered image of that frame
     */
    BufferedImage getNextFrame();
    
    /**
     * This will change the frame that the animation is
     * currently on so that the next time getNextFrame is
     * called, it will return that frame
     * @param n The frame to change to
     */
    void setFrame (int n);
    
    /**
     * Is this animation at the last frame?
     * @return Whether it is at the last frame
     */
    boolean isAtEnd();
    
}