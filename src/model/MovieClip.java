package model;

import java.awt.Point;
import java.awt.image.BufferedImage;

import model.animation.AnimationSwapper;
import model.animation.IteratingAnimation;
//import model.utils.Utils;

/**
 * A movie clip is a static image, an animation, or collection of animations to
 * be added to the game. It can be played, paused, animation swapped, moved,
 * rotated, and scaled. See the full documentation to see how the constructors
 * work fully.
 * @author Nick
 *
 */
public class MovieClip implements IteratingAnimation, MovieClipInterface {
    /** 
     * Each movie clip is given a unique id which
     * determines if two movie clips are equal
     */
    private static int currID = 0;

    /** unique identifier to determine movie clip equality */
    protected int id;

    /** Directly alter this to change x position */
    public double x;
    /** Directly alter this to change y position */
    public double y;
    /** Directly alter this to change rotation in radians */
    public double rotation;
    /** Directly alter this to change x scale. 1 = normal*/
    public double scaleX;
    /** Directly alter this to change y scale. 1 = normal*/
    public double scaleY;
    

    /** shift added to all images, for use in setOrigin */
    public int shiftX;
    public int shiftY;
    
    /** whether the last frame will lead to replay */
    private boolean looping;
    /** whether the last frame will lead to deletion */
    private boolean removing;

    public Model parent;

    private AnimationSwapper anims;
    
    public int bonusVariable;

    /**
     * Constructor for MovieClip. Movie clip takes in an animation
     * or set of animations. If you want to pass in a single folder of frames,
     * give the path, then set multipleAnims to false. If you want to give
     * a folder that contains multiple folders, each with animation frames,
     * give that path and set multipleAnims to true.
     * Note: for any given sub-folder, it can instead be a static image, without
     * a folder. In this case you swap to it by saying swapAnimation("image.png")
     * @param path Path to folder of animations, or folder of folders
     * @param multipleAnims Is this a folder of folders?
     */
    public MovieClip(String path, boolean multipleAnims) {
        if (multipleAnims){
            anims = new AnimationSwapper(path);
        }else{
            anims = new AnimationSwapper("default",path);
        }
        x = 0;
        y = 0;
        shiftX = 0;
        shiftY = 0;
        rotation = 0;
        scaleX = 1;
        scaleY = 1;
        looping = true;
        removing = false;
        id = getNextID();
    }
    
    /**
     * Constructor for MovieClip if you only have one animation. This
     * takes in the path to the folder that contains the frames.
     * You can also insert the path to a single image, in which
     * case the movie clip will show that image and not animate.
     * @param path Location of the folder of frames.
     */
    public MovieClip(String path) {
        this(path, false);
    }
    
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
    @Override
    public void setOrigin(int ox, int oy){
        shiftX = -ox;
        shiftY = -oy;
    }
    
    /**
     * This will switch the animation to a different one with a given
     * name. It will RESTART that animation and set its direction for FORWARD
     * The name is determined by the name of the folder that contains
     * the frames. If it is a static image, insert the name+extension.
     * @param name The name of the animation to switch to
     */
    @Override
    public void swapAndRestart(String name) {
        anims.swapAnimation(name);
        gotoAndPlay(0);
    }
    
    /**
     * This will switch the animation to a different one with a given
     * name. It will CONTINUE at the frame this specific animation was on previously.
     * It will not alter direction or whether it was paused.
     * The name is determined by the name of the folder that contains
     * the frames. If it is a static image, insert the name+extension.
     * @param name The name of the animation to switch to
     */
    @Override
    public void swapAndResume(String name) {
        anims.swapAnimation(name);
    }
    
    /**
     * This will determine if the current movie clip is overlapping
     * another movie clip determined by the rectangular boundaries
     * of their images.
     * @param mc The movie clip to test against
     * @return Whether they are overlapping
     */
    @Override
    public boolean hitTest(MovieClip mc){
        /*//maybe top left is off
        Point cd = getDimensions();
        Point mcd = mc.getDimensions();
        int sx = (int) (shiftX * scaleX);
        int sy = (int) (shiftY * scaleY);
        int sx2 = (int) (mc.shiftX * mc.scaleX);
        int sy2 = (int) (mc.shiftY * mc.scaleY);
        return Utils.rect_collision(new Point(x+sx,y+sy), cd.x, cd.y, 
                                    new Point(mc.x+sx2,mc.y+sy2), mcd.x, mcd.y);*/
        return false;
    }
    
    
    /**
     * This will determine if the current movie clip is overlapping
     * another movie clip determined by shaped boundaries. The only
     * option currently is circle, which fits circles inside the boundaries
     * of both movie clips and checks for collision.
     * @param mc The movie clip to test against
     * @param shape Use "circle"
     * @return Whether they are overlapping
     */
    @Override
    public boolean hitTest(MovieClip mc, String shape){
        /*if(shape == "circle"){
            Point cd = getDimensions();
            Point mcd = mc.getDimensions();
            int r1 = (int) (cd.x/2);
            int r2 = (int) (mcd.x/2);
            int sx = (int) (shiftX * scaleX);
            int sy = (int) (shiftY * scaleY);
            int sx2 = (int) (mc.shiftX * mc.scaleX);
            int sy2 = (int) (mc.shiftY * mc.scaleY);
            Point center1 = new Point(x+sx+r1,y+sy+r1);
            Point center2 = new Point(mc.x+sx2+r2,mc.y+sy2+r2);
            return Utils.circle_collision(center1, r1, center2, r2);
        }*/
        return false;
    }
    
    /**
     * This will determine if the current movie clip overlaps a given
     * point, using rectangular boundaries.
     * @param p The point to test against
     * @return Whether it overlaps
     */
    @Override
    public boolean hitTest(Point p){
        Point cd = getDimensions();
        int sx = (int) (shiftX * scaleX);
        int sy = (int) (shiftY * scaleY);
        boolean xbounded = x+sx <= p.x && p.x <= x+sx+ cd.x;
        boolean ybounded = y+sy <= p.y && p.y <= y+sy+ cd.y; 
        return xbounded && ybounded;
    }
    
    /**
     * This will determine if the current movie clip is overlapping
     * a point determined by shaped boundaries. The only
     * option currently is circle, which fits circles inside the boundaries
     * of the movie and checks for collision.
     * @param p The point to test against
     * @param shape Use "circle"
     * @return Whether they are overlapping
     */
    @Override
    public boolean hitTest(Point p, String shape){
       /* if(shape == "circle"){
            Point cd = getDimensions();
            int r1 = (int) (cd.x/2);
            int sx = (int) (shiftX * scaleX);
            int sy = (int) (shiftY * scaleY);
            Point center1 = new Point(x+sx+r1,y+sy+r1);
            return Utils.circle_collision(center1, r1, p, 0);
        }*/
        return false;
    }
    
    /**
     * This will alter the rotation of an object to have its right side aim
     * towards a given point.
     * @param p Point to aim towards
     */
    @Override
    public void aimAt(Point p){
        int xdif = (int) (p.x - x);
        int ydif = (int) (p.y - y);
        rotation = Math.atan2(ydif, xdif);
    }
    
    
    /**
     * This will increment the current frame of the current animation to the next 
     * image in the sequence and return the buffered image of that frame. 
     * The "next" image depends on what the direction is set to.
     * @return The buffered image at the next frame
     */
    @Override
    public BufferedImage getNextFrame() {
        BufferedImage result =  anims.getNextFrame();
        if(!looping && anims.isAtEnd()){
            setDirection(Direction.PAUSED);
        }
        
        if(removing && anims.isAtEnd()){
            parent.removeNextTime(this);
        }
        return result;
    }

    /**
     * This will change the current frame to the one given
     * @param n The frame to switch to
     */
    @Override
    public void setFrame(int n) {
        anims.setFrame(n);
    }
    
    /**
     * Is this animation at the last frame?
     * @return Whether it is at the last frame
     */
    @Override
    public boolean isAtEnd(){
        return anims.isAtEnd();
    }

    /**
     * Sets the direction of the animations. Follows the same rules
     * as MovieClip.setDirection. All animations henceforth will
     * follow this direction as well.
     */
    @Override
    public void setDirection(Direction dir) {
        anims.setDirection(dir);
        looping = true;
    }
    
    /**
     * This will cause the animation, and future animations to play.
     */
    @Override
    public void play(){
        anims.setDirection(Direction.FORWARD);
        looping = true;
    }
    
    /**
     * This will cause the animation, and future animations to stop.
     */
    @Override
    public void stop(){
        anims.setDirection(Direction.PAUSED);
    }
    
    /**
     * This will set the animation to go to a given frame, then play.
     * @param n The frame to go to
     */
    @Override
    public void gotoAndPlay(int n){
        anims.setDirection(Direction.FORWARD);
        anims.setFrame(n);
    }
    
    /**
     * This will set the animation to go to a given frame, then stop.
     * @param n The frame to go to
     */
    @Override
    public void gotoAndStop(int n){
        anims.setDirection(Direction.PAUSED);
        anims.setFrame(n);
    }
    
    /**
     * This will cause the animation to pause when it reaches its last
     * frame. It can be resumed with the play() command
     */
    @Override
    public void stopAtEnd(){
        looping = false;
    }
    
    /**
     * This will cause the animation to delete itself when it reaches its last
     * frame.
     */
    @Override
    public void removeAtEnd(){
        removing = true;
    }
    
    /**
     * This will return the current playing animation
     * @return A string that represents the animation name
     */
    @Override
    public String getCurrentAnim(){
        return anims.currentAnimName;
    }

    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof MovieClip)){
            return false;
        }
        MovieClip mc = (MovieClip) obj;
        return id == mc.id;
    }
    
    /**
     * Get dimensions of currently playing animation
     * @return A point (width,height)
     */
    public Point getDimensions () {
        int dimx =  (int) (anims.getDimensions().x * scaleX);
        int dimy =  (int) (anims.getDimensions().y * scaleY);
        return new Point(dimx,dimy);
    }
    
    public boolean isTransformed () {
        return rotation != 0 || scaleX != 1 || scaleY != 1;
    }
    
    /**
     * Returns unique ID number, for use of movie clip IDs
     * @return A unique ID number
     */
    public static int getNextID(){
        currID ++;
        return currID;
    }
}
