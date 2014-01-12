package controller.advancedtools;

/**
 * This takes the "derivative" of a boolean value.
 * Here is a sample of stream of input, alongside the
 * output it would produce:
 * F, F, F, F, F, F, T, T, T, T, F, T, F, F, F, F, F
 * 0, 0, 0, 0, 0, 0, 1, 0, 0, 0,-1, 1,-1, 0, 0, 0, 0
 * If a value has just changed to true, it will output 1 
 * (similarly -1 for false).
 * It will always output 0 on the first access.
 * 
 * The user calls getDelta and give input through that function.
 * In turn, that function will return its output.
 * 
 * An example of usage would be an event where a character enters
 * an area. You want to perform an action ONCE every time the player
 * leaves an area.
 * You would simply say:
 * if (myDeriv.getDelta(player.hitTest(area)) == -1){
 *     perform action
 * }
 * @author Nick
 *
 */
public class Derivative {

    private boolean lastInput = false;
    private boolean firstAccess = true;
    
    /**
     * Given input as the next value in a stream of input, it
     * will return the derivative.
     * @param input true or false
     * @return -1,0,1 as the derivative
     */
    public int getDelta(boolean input){
        int result = 0;
        if (input != lastInput && !firstAccess){
            result = (input ? 1 : -1);
        }
        lastInput = input;
        firstAccess = false;
        return result;
    }

}
