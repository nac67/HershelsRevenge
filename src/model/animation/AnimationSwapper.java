package model.animation;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

import model.utils.Utils;

/**
 * The Animation Swapper makes it easy to have an object represented
 * by a number of animations. Then you can swap between the animations.
 * Take for example a player who can be standing, or running.
 * The constructor can take in a toplevel folder which contains folders which contain
 * the animation frames and automatically load those into animations 
 * with the name of the folder
 * 
 * toplevel folder
 *      |
 *      |----anim1 - many images
 *      |     
 *      |----anim2 - many images
 *      
 * @author Nick Cheng
 *
 */
public class AnimationSwapper implements IteratingAnimation {
    
    /** A set of animations and names for those animations */
    Hashtable<String, Animation> animationSet;
    /** The direction to use for all animations */
    Direction dir = Direction.FORWARD;
    Animation currentAnim;
    public String currentAnimName;


    /**
     * Constructor with one default animation. This animation will
     * be automatically set as the currentMovie so getNextFrame
     * will return the first frame of it.
     * @param name The name you wish to link to the animation
     * @param dirpath The folder which contains the frames of the
     * animation.
     */
    public AnimationSwapper(String name, String dirpath) {
        animationSet = new Hashtable<String, Animation>();
        loadAnimation(name,dirpath);
    }

    /**
     * Constructor with a toplevel folder. This will go through all of the
     * folders and make each one into a animation with the same name as
     * the folder name. The name that comes first alphabetically will
     * be set as the currentMovie. The sub-folders could also be individual
     * images. In which case there will be animations and static images to
     * swap between.
     * @param dirpath The string representation of the toplevel directory
     */
    public AnimationSwapper(String dirpath){
        //take top level directory and load in lots of animations from folder
        animationSet = new Hashtable<String, Animation>();

        //TODO here
        File folder = new File(Utils.currentDir()+dirpath);
        
        boolean didFirst = false;
        
        try {
            for (final File fileEntry : folder.listFiles()) {
                String name = fileEntry.getName();
                Animation an = new Animation(fileEntry);
                animationSet.put(name, an);
                if(!didFirst){
                    currentAnim = an;
                    currentAnimName = name;
                    didFirst = true;
                }
            }
        }catch (Exception e){
            System.err.println("Failed to read directory: "+dirpath);
            System.exit(1);
        }
    }

    /**
     * Adds a single animation to the list of available animations to swap between
     * If this is the first animation loaded, it will be set as the currentMovie
     * @param name The name you wish to link to the animation
     * @param dirpath The string representation of the folder that contains
     * the animation frames
     */
    public void loadAnimation(String name,String dirpath){
        Animation newAnim = new Animation(dirpath);
        animationSet.put(name, newAnim);
        if(animationSet.size() == 1){
            currentAnim = newAnim;
            currentAnimName = name;
        }
    }

    /**
     * Sets the direction of the animations. Follows the same rules
     * as MovieClip.setDirection. All animations henceforth will
     * follow this direction as well.
     */
    @Override
    public void setDirection(Direction dir) {
        this.dir = dir;
        currentAnim.setDirection(dir);
    }
    
    /**
     * Changes the currentMovie to a different animation so that
     * when getNextFrame is called, a frame from this animation will
     * be returned. Note: it will be either the name of the folder
     * containing the frames, or just the file name+extension if
     * it's a static image.
     * @param name The name of the animation to swap to
     */
    public void swapAnimation(String name){
        if(!animationSet.containsKey(name)){
            System.err.println("invalid animation name: "+name);
            return;
        }
        if (currentAnimName != name){
            currentAnim = animationSet.get(name);
            currentAnim.setDirection(dir);
            currentAnimName = name;
        }
    }

    /**
     * This will increment the current frame of the current animation to the next 
     * image in the sequence and return the buffered image of that frame. 
     * The "next" image depends on what the direction is set to.
     * @return The buffered image at the next frame
     */
    @Override
    public BufferedImage getNextFrame() {
        return currentAnim.getNextFrame();
    }
    
    /**
     * This will change the current frame to the one given
     * @param n The frame to switch to
     */
    @Override
    public void setFrame(int n) {
        currentAnim.setFrame(n);
    }
    
    /**
     * This will give the dimensions of the current animation
     * @return a Point of width,height
     */
    public Point getDimensions(){
        return currentAnim.getDimensions();
    }
    
    /**
     * Is this animation at the last frame?
     * @return Whether it is at the last frame
     */
    @Override
    public boolean isAtEnd(){
        return currentAnim.isAtEnd();
    }
}
