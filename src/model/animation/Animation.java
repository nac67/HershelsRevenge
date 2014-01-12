package model.animation;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import model.Constants;

/**
 * The Animation class is very helpful for iterating through
 * a sequence of frames that describe an animation. You can simply
 * pass in a directory path and it will load up all of the images
 * sequentially and prepare them for playback.
 * @author Nick Cheng
 */
public class Animation implements IteratingAnimation {
    /**
     * This hashtable contains all of the loaded animations for the game.
     * The purpose of storing them is so that if an movie clip is created multiple
     * times with the same animation, the first one will be loaded from disk,
     * but all subsequent movie clips will lookup the animation from this table.
     * 
     * Specifically, this hashtable uses relative path as the key, and
     * the sequence of images as values.
     */
    public static Hashtable<String,ArrayList<BufferedImage>> loadedAnimations;
    
    /**The list of frames stored as buffered images */
    ArrayList<BufferedImage> images;
    public int currentFrame = 0;
    private Direction dir = Direction.FORWARD;
    private Point dimensions;

    
    
    /**
     * Constructor that takes in the path of a directory (or individual image) and will load
     * the animations into a list for easy playback. Make sure that the
     * images inside the folder are in order alphabetically.
     * @param folder A File object that represents the folder which contains
     * the animation pictures, or individual image
     */
    public Animation(File folder) {
        String path = folder.getPath();
        if(loadedAnimations.containsKey(path)){
            images = loadedAnimations.get(path);
        }else{
            images = new ArrayList<BufferedImage>();
            
            try {
                if (folder.isDirectory()){
                    if(Constants.DEBUG) System.out.println("Reading dir: "+path);
                    File[] folderContents = folder.listFiles();
                    Arrays.sort(folderContents); //some platforms don't auto-sort (e.g. Ubuntu)
                    for (final File fileEntry : folderContents) {
                        if (fileEntry.isDirectory()){ 
                            System.err.println("Expecting image but got directory when reading:\n"
                                    + fileEntry.getPath() 
                                    +"\nDid you forget to use the MovieClip(path, true)"
                                    + "\nconstructor to specify that it has subfolders?\n");
                            System.exit(1);
                            
                        }
                        images.add(ImageIO.read(fileEntry));
                    }
                }else{
                    //folder is actually a file, just read it
                    if(Constants.DEBUG) System.out.println("Reading image: "+path);
                    images.add(ImageIO.read(folder));
                }
            } catch (IOException e) {
                System.err.println("Error Reading image: "+path);
                System.exit(1);
            }
            loadedAnimations.put(path, images);
        }
        //read dimensions of animation from first image in sequence
        dimensions = new Point(images.get(0).getWidth(),images.get(0).getHeight());
    }

    /**
     * Constructor that takes in the path of a directory (or individual image) and will load
     * the animations into a list for easy playback. Make sure that the
     * images inside the folder are in order alphabetically.
     * @param path A string representation of the path of the folder (or individual image) which
     * contains the animation pictures
     */
    public Animation(String path) {
        this(new File(path));
    }
    
    /**
     * This will increment the current frame to the next image in the sequence
     * and return the buffered image of that frame. The "next" image depends on what
     * the direction is set to.
     * @return The buffered image at the next frame
     */
    @Override
    public BufferedImage getNextFrame() {
        currentFrame = ((currentFrame+dir.speed) + images.size())% images.size();
        return images.get(currentFrame);
    }
    
    /**
     * This will change the current frame to the one given
     * @param n The frame to switch to
     */
    @Override
    public void setFrame(int n) {
        currentFrame =  (n + images.size())% images.size();
    }

    /**
     * This will change the direction of the animation playback.
     * The options are:
     * Direction.FORWARD - normal playback,
     * Direction.BACKWARD - reverse playback,
     * Direction.PAUSED - the animation will stay put.
     * @param dir The desired direction
     */
    @Override
    public void setDirection(Direction dir) {
        this.dir = dir;
    }
    
    /**
     * This will give the dimensions of the animation, assuming
     * all the frames are the same dimension
     * @return a Point of width,height
     */
    public Point getDimensions(){
        return dimensions;
    }
    
    /**
     * Is this animation at the last frame?
     * @return Whether it is at the last frame
     */
    @Override
    public boolean isAtEnd(){
        return currentFrame == images.size()-1;
    }
}
