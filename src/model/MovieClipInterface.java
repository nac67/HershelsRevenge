package model;

import java.awt.Point;

public interface MovieClipInterface {
    
    //You can also set x,y positions explicity
    
    /**
     * Relatively speaking, this is where you consider
     * the center of the image to be (i.e. The position
     * you wish (x,y) to refer to).
     * This will also be the location that the object rotates around
     * and stretches around.
     * More specifically, this will alter the shift values, so that
     * upon painting, it will apply a shift to the location
     * of the image. The hit test methods are corrected for this shift too.
     * @param ox x origin in pixels
     * @param oy y origin in pixels
     */
    public void setOrigin(int ox, int oy);
    
    /**
     * This will switch the animation to a different one with a given
     * name. It will RESTART that animation and set its direction for FORWARD
     * The name is determined by the name of the folder that contains
     * the frames. If it is a static image, insert the name+extension.
     * @param name The name of the animation to switch to
     */
    public void swapAndRestart(String name);
    
    /**
     * This will switch the animation to a different one with a given
     * name. It will CONTINUE at the frame this specific animation was on previously.
     * It will not alter direction or whether it was paused.
     * The name is determined by the name of the folder that contains
     * the frames. If it is a static image, insert the name+extension.
     * @param name The name of the animation to switch to
     */
    public void swapAndResume(String name);
    
    /**
     * This will determine if the current movie clip is overlapping
     * another movie clip determined by the rectangular boundaries
     * of their images.
     * @param mc The movie clip to test against
     * @return Whether they are overlapping
     */
    public boolean hitTest(MovieClip mc);
    public boolean hitTest(MovieClip mc, String shape);
    public boolean hitTest(Point p);
    public boolean hitTest(Point p, String shape);
    
    /**
     * This will alter the rotation of an object to have its right side aim
     * towards a given point.
     * @param p Point to aim towards
     */
    public void aimAt(Point p);
    
    /**
     * This will cause the animation, and future animations to play.
     */
    public void play();
    
    /**
     * This will cause the animation, and future animations to stop.
     */
    public void stop();
    
    /**
     * This will set the animation to go to a given frame, then play.
     * @param n The frame to go to
     */
    public void gotoAndPlay(int n);
    
    /**
     * This will set the animation to go to a given frame, then stop.
     * @param n The frame to go to
     */
    public void gotoAndStop(int n);

    /**
     * This will cause the animation to pause when it reaches its last
     * frame. It can be resumed with the play() command
     */
    public void stopAtEnd();

    /**
     * This will cause the animation to delete itself when it reaches its last
     * frame.
     */
    public void removeAtEnd();
    
    /**
     * This will return the current playing animation
     * @return A string that represents the animation name
     */
    public String getCurrentAnim();
}
