package controller.advancedtools;

/**
 * This is a small frame based timer for use in the game loop.
 * It will only keep track of how many frames elapsed if you call 
 * isReady EVERY frame.
 * @author Nick Cheng
 *
 */
public class MiniTimer {
    
    private int t = 0;
    private int frameGap;
    private int repeats;
    private boolean infinite;
    

    /**
     * You create the timer by saying how often it
     * will fire, and how many repetitions to do.
     * 0 repetitions will loop forever.
     * @param frameGap Frames in between each fire
     * @param repeats How many times to repeat (0=forever)
     */
    public MiniTimer(int frameGap, int repeats) {
        this.frameGap = frameGap;
        this.repeats = repeats;
        infinite = (repeats == 0);
    }
    
    
    /**
     * You create the timer by saying how often it
     * will fire. The timer will loop forever.
     * @param frameGap Frames in between each fire
     */
    public MiniTimer(int frameGap) {
        this(frameGap,0);
    }
    
    /**
     * isReady should be called every time step. It
     * will return true every frameGap number of frames
     * until it runs out of repetitions (or never).
     * It will only keep track of how many frames elapsed
     * if you call isReady EVERY frame.
     * @return Whether the timer should fire
     */
    public boolean isReady () {
        if(infinite || repeats > 0){
            t ++;
            if(t >= frameGap){
                repeats --;
                t = 0;
                return true;
            }
        }
        return false;
    }
    
    /**
     * This switches the interval between each
     * repetition. If the timer is on frame 10 of
     * a 15 frame timer, and the user switches the
     * interval to 5, it will trigger the next time
     * isReady is called then reset.
     * @param frames The new interval
     */
    public void setGap(int frames){
        frameGap = frames;
    }

}
