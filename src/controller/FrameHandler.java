package controller;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import model.Constants;
import model.MovieClip;
import model.Pathfinding;
import model.Tile;
import model.animation.IteratingAnimation.Direction;
import model.utils.DPoint;
import model.utils.Utils;
import model.Model;
import model.Model.State;

/**
 * This is the frame handler. It contains the main game loop which
 * runs at a fixed rate determined in Constants.
 * @author Nick Cheng
 */
public class FrameHandler implements ActionListener{
    

    private Controller controller;
    private Model model;
    
    private AbstractMouseHandler mouse;
    private KeyHandler keys;
    
    //private long lastTime;
    
    public FrameHandler(Controller controller, Model model) {
        if(Constants.DEBUG) System.out.println("frame handler init, ready.");
        this.controller = controller;
        this.model = model;
        mouse = controller.gameMouse;
        keys = controller.gameKeys;
    }
    
    public void actionPerformed(ActionEvent e) {
       synchronized(model){
           keys.updateKeyPresses();
           mouse.updateDeriv();
           enterFrame();
           controller.notifyView();
           //if (lastTime != 0) System.out.println(System.currentTimeMillis()-lastTime);
           //lastTime = System.currentTimeMillis();
       }
    }
    
    /**
     * The main game loop which runs every TIME_STEP milliseconds
     */
    private void enterFrame(){
        
        if(model.gameState == State.WAIT_FOR_PLAYER || model.gameState == State.MOVE_CHARACTERS){
            determineMouseOrKeys();
            model.levelTimer++;
        }
        
        if(model.gameState == State.MAIN_MENU){
            if(!model.menuReady){
                model.menuReady = true;
                model.drawMainMenu();
            }
            checkButton(model.instructions_btn);
            checkButton(model.select_btn);
        }else if(model.gameState == State.INSTRUCTIONS){
            if(!model.menuReady){
                model.menuReady = true;
                model.drawInstructions();
            }
            checkButton(model.select_btn);
        }else if(model.gameState == State.LEVEL_SELECT){
            if(!model.menuReady){
                model.menuReady = true;
                model.drawLevelSelect();
            }
            checkButton(model.instructions_btn);
            checkLevelTicks();
        }else if(model.gameState == State.PAUSED){
            checkButton(model.continue_btn);
            checkButton(model.select_btn);
        }else if (model.gameState == State.WAIT_FOR_PLAYER){
            if(keys.keyJustPressed(27)){
                model.savedState = model.gameState;
                model.gameState = State.PAUSED;
                model.drawPauseMenu();
            }
            
            calcLastLight();
            
            aimPlayer();
            calcCurrentLight();

            updateChupaLight();
            
            //to avoid last light being incorrect, player cannot move and turn on same frame
            if(!model.playerHasTurned){
                
                Point newPosition = new Point(model.player.col,model.player.row);
                boolean keyPressed = false;
                
                if (keys.isDown(65)){
                    newPosition = new Point(newPosition.x-1,newPosition.y);
                    keyPressed = true;
                }else if (keys.isDown(68)){
                    newPosition = new Point(newPosition.x+1,newPosition.y);
                    keyPressed = true;
                }else if (keys.isDown(83)){
                    newPosition = new Point(newPosition.x,newPosition.y+1);
                    keyPressed = true;
                }else if (keys.isDown(87)){
                    newPosition = new Point(newPosition.x,newPosition.y-1);
                    keyPressed = true;
                }
                
                
                //if(keys.keyJustPressed(66)) model.completeLevel();
                
                boolean validMove = model.gameboard.inBounds(newPosition.y, newPosition.x) &&
                        !model.gameboard.walls[newPosition.y][newPosition.x];
                
                if (keyPressed && validMove){
                    model.gameboard.calculateBits(model.player, newPosition);
                    model.player.row = newPosition.y;
                    model.player.col = newPosition.x;
                    calcCurrentLight();
                    calcEnemyMovement();

                    playerWalkAnimation();
                    
                    model.stepSound.play();
                    
                    model.gameboard.sortDepth(2);
                    model.moveTimer = 0;
                    model.gameState = State.MOVE_CHARACTERS;
                }
            }
            
            updateLightAnims();
        }
        else if (model.gameState == State.MOVE_CHARACTERS){
            if(keys.keyJustPressed(27)){
                model.savedState = model.gameState;
                model.gameState = State.PAUSED;
                model.drawPauseMenu();
            }
            if(model.moveTimer < Constants.MOVE_TIME){
                model.player.moveABit();
                
                for(Tile enemy : model.enemies){
                    enemy.moveABit();
                }
                
                updateLightAnims();

                model.moveTimer ++;
            }else{
                boolean enemyTouchPlayer = Utils.isInTileList(model.player, model.enemies);
                boolean touchBattery = model.player.row == model.battery.row && model.player.col == model.battery.col;
                if(enemyTouchPlayer){
                    model.moveTimer = 0;
                    model.gameOverEnemy = enemyAt(model.player.col,model.player.row);
                    
                    //if(model.gameOverEnemy.getCurrentAnim() != "ChupaHide"){
                    
                    model.removeChild(model.gameOverEnemy);
                    if(model.gameOverEnemy.getCurrentAnim() == "ChupaHide"){
                        model.player.swapAndRestart("HershelDieBelow");
                        model.player.setOrigin(60, 80);
                        model.player.stopAtEnd();
                        model.dieTime = Constants.DIE_TIME2;
                        model.die2Sound.play();
                    }else{
                        model.player.swapAndRestart("HershelDieAbove");
                        model.player.setOrigin(40, 66);
                        model.player.stopAtEnd();
                        model.dieTime = Constants.DIE_TIME;
                        model.die1Sound.play();
                    }
                    model.flashlight = new Area();
                    model.gameState = State.GAME_OVER;
                    /*}else{
                        model.removeChild(model.gameOverEnemy);
                        model.enemies.remove(model.gameOverEnemy);
                        //same as before
                        model.gameboard.realPosition(model.player);
                        for(Tile enemy : model.enemies){
                            model.gameboard.realPosition(enemy);
                            chupaIdleAnimation(enemy);
                        }
                        
                        playerIdleAnimation();
                        model.gameState = State.WAIT_FOR_PLAYER;
                    }*/
                }else if(touchBattery){
                    model.removeChild(model.battery);
                    model.completeLevel();                
                }else{
                    //set to real position so that inaccuracies with
                    //movement don't pile up
                    model.gameboard.realPosition(model.player);
                    for(Tile enemy : model.enemies){
                        model.gameboard.realPosition(enemy);
                        chupaIdleAnimation(enemy);
                    }
                    
                    playerIdleAnimation();
                    model.gameState = State.WAIT_FOR_PLAYER;
                }
            }
        }
        else if(model.gameState == State.GAME_OVER){
            if (model.moveTimer< model.dieTime){
                model.moveTimer ++;
            }else{
                model.player.setOrigin(60, 80);
                model.player.play();
                model.loadLevel(model.currentLevel);
            }
        }
        if(model.gameState != State.MAIN_MENU && model.gameState != State.INSTRUCTIONS && model.gameState != State.LEVEL_SELECT){
            smoothScrollBackground();
        }
    }

    private void checkButton(MovieClip btn) {
        if(btn.hitTest(mouse.mouseLocLocal())){
            btn.gotoAndStop(0);
            
            if(mouse.leftJustPressed){
                if(btn == model.instructions_btn){
                    model.gameState = State.INSTRUCTIONS;
                    model.menuReady = false;
                }
                if(btn == model.select_btn){
                    model.gameState = State.LEVEL_SELECT;
                    model.menuReady = false;
                }
                if(btn == model.continue_btn){
                    model.gameState = model.savedState;
                    model.menuReady = false;
                    model.clearMenus();
                    model.removeChild(model.black);
                }
            }
            
            
        }else{
            btn.gotoAndStop(1);
        }
    }

    
    private void checkLevelTicks() {
        if(mouse.leftJustPressed){
            for(MovieClip tick : model.menuLevelTicks){
                if(tick.hitTest(mouse.mouseLocLocal())){
                    model.menuReady = false;
                    model.gameState = State.WAIT_FOR_PLAYER;
                    model.loadLevel(tick.bonusVariable);
                    model.currentLevel = tick.bonusVariable;
                    return;
                }
            }
        }
    }
    /**
     * Should game treat mouse or key input?
     */
    private void determineMouseOrKeys() {
        if (!mouse.mouseLoc().equals(model.lastMouse)&&model.lastMouse!=null&&mouse.isOnScreen()){
            model.useMouse = true;
        }
        Point2D.Double p = keys.arrowVector();
        if(p.x != 0.0 || p.y != 0.0){
            model.useMouse = false;
        }
    }


    private int holdTimer = 0;
    private void smoothScrollBackground() {
        int look = Constants.KEYBOARD_LOOK_REACH;
        double ease = 2;

        double targetX =(model.player.x )+model.gameboard.visualX+model.gameboard.width/2;
        double targetY = (model.player.y)+model.gameboard.visualY+model.gameboard.height/2;

        if(!model.useMouse){
            Point2D.Double arrow = keys.arrowVector();
            if(arrow.x!= 0.0 || arrow.y!=0.0){
                holdTimer ++;
                if(holdTimer > 6){
                    targetX += arrow.x*look;
                    targetY += arrow.y*look;
                }
            }else{
                holdTimer =0;
            }
        }else{
            if(mouse.leftButton && model.levelTimer>10){
                int tileCenterX = (model.gameboard.visualX + model.gameboard.width)/2;
                int tileCenterY = model.gameboard.visualY + model.gameboard.height/2;
                int xdiff = (int) (mouse.mouseX()-model.levelShiftX- (model.player.x + tileCenterX));
                int ydiff = (int) (mouse.mouseY()-model.levelShiftY- (model.player.y + tileCenterY));
                DPoint vector = model.gameboard.xyToTile(xdiff, ydiff);
                double y = vector.y;
                double x = vector.x;
                if(y < x && y < -x) targetY -= look;
                if(y > x && y > -x) targetY += look;
                if(x < y && x < -y) targetX -= look;
                if(x > y && x > -y) targetX += look;
            }
        }
        
        
        double idealX = -targetX + Constants.GAME_WIDTH/2;
        double idealY = -targetY + Constants.GAME_HEIGHT/2;
        
        
        
        model.levelShiftX += (int) ((idealX - model.levelShiftX) /ease);
        model.levelShiftY += (int) ((idealY - model.levelShiftY) /ease);
    }
    

    private Tile enemyAt(int col, int row) {
        for(Tile enemy : model.enemies){
            if (enemy.row == row && enemy.col == col) return enemy;
        }
        return null;
    }

    private void playerIdleAnimation() {
        if(model.player.dir == Tile.Direction.LEFT){
            model.player.swapAndResume("HershelLeftIdle");
        }
        if(model.player.dir == Tile.Direction.RIGHT){
            model.player.swapAndResume("HershelRightIdle");
        }
        if(model.player.dir == Tile.Direction.UP){
            model.player.swapAndResume("HershelBackIdle");
        }
        if(model.player.dir == Tile.Direction.DOWN){
            model.player.swapAndResume("HershelFrontIdle");
        }
    }

    private void playerWalkAnimation() {
        if(model.player.dir == Tile.Direction.LEFT){
            model.player.swapAndRestart("HershelLeftWalk");
        }
        if(model.player.dir == Tile.Direction.RIGHT){
            model.player.swapAndRestart("HershelRightWalk");
        }
        if(model.player.dir == Tile.Direction.UP){
            model.player.swapAndRestart("HershelBackWalk");
        }
        if(model.player.dir == Tile.Direction.DOWN){
            model.player.swapAndRestart("HershelFrontWalk");
        }
    }

    private void calcLastLight() {
        model.lastLight.clear();
        for(Point p : model.currLight){
            model.lastLight.add(new Point(p.x,p.y));
        }
    }

    private void aimPlayer() {
        int NONE = -1;
        int UP = 0;
        int DOWN = 1;
        int LEFT = 2;
        int RIGHT = 3;
        
        int dir = NONE;
        //boolean useMouse = model.lastMouse != null && !mouse.mouseLoc().equals(model.lastMouse);
        boolean useMouse = model.useMouse;
        
        //model.useMouse = model.lastMouse != null && !mouse.mouseLoc().equals(model.lastMouse);
        //boolean useMouse = model.useMouse;
        
        model.lastMouse = mouse.mouseLoc();
        if(useMouse){
            int tileCenterX = (model.gameboard.visualX + model.gameboard.width)/2;
            int tileCenterY = model.gameboard.visualY + model.gameboard.height/2;
            int xdiff = (int) (mouse.mouseX()-model.levelShiftX- (model.player.x + tileCenterX));
            int ydiff = (int) (mouse.mouseY()-model.levelShiftY- (model.player.y + tileCenterY));
            DPoint vector = model.gameboard.xyToTile(xdiff, ydiff);
            double y = vector.y;
            double x = vector.x;
            if(y < x && y < -x) dir = UP;
            if(y > x && y > -x) dir = DOWN;
            if(x < y && x < -y) dir = LEFT;
            if(x > y && x > -y) dir = RIGHT;
        }else{
            if(keys.isDown(37)) dir = LEFT;
            if(keys.isDown(38)) dir = UP;
            if(keys.isDown(39)) dir = RIGHT;
            if(keys.isDown(40)) dir = DOWN;
        }
        
        model.playerHasTurned = false;
        //only allowed to change direction if:
        //    game initialized
        //    direction is different than last direction (not including none)
        if(model.lastDir == -2 || model.lastDir != dir && dir != NONE){
            
            if(dir == UP){
                model.player.dir = Tile.Direction.UP;
            }
            if(dir == DOWN){
                model.player.dir = Tile.Direction.DOWN;
            }
            if(dir == LEFT){
                model.player.dir = Tile.Direction.LEFT;
            }
            if(dir == RIGHT){
                model.player.dir = Tile.Direction.RIGHT;
            }
            
            playerIdleAnimation();
            model.playerHasTurned = true;
        }
        if(dir != NONE)
            model.lastDir = dir;
        
    }
    
    private void calcEnemyMovement() {
        //since enemies determine movement one at a time
        //reverse list to avoid certain ones getting special treatment
        Collections.reverse(model.enemies);
        for (Tile enemy : model.enemies){
            
            //distance from player
            int xdis = (int) (enemy.x - model.player.x);
            int ydis = (int) (enemy.y - model.player.y);
            int dist = (int) Math.sqrt(xdis*xdis + ydis*ydis);
            boolean closeEnough = dist < 550;
            
            
            enemy.bitX = 0;
            enemy.bitY = 0;
            boolean enemyWasInLight = Utils.isInList(enemy, model.lastLight);
            if(!enemyWasInLight && closeEnough){
                //CALCULATE PATH TO PLAYER
                boolean[][] wallsAndEnemies = model.gameboard.wallsAndEnemies();
                Point start = new Point(enemy.col,enemy.row);
                Point end = new Point(model.player.col,model.player.row);
                LinkedList<Point> path = Pathfinding.findPath(wallsAndEnemies, start, end, true, 1);
               
                
                if(path != null && path.size()>0){
                    Point nextHop = path.getFirst();
                    
                    
                    /*
                    //attempt 1 : enemy can't move if:
                    //he was in the light before movement or
                    //his next hop will put him in light
                    //
                    //this ended up being too hard 
                    
                    boolean nextHopCurrInLight = Utils.isInList(nextHop, model.currLight);
                    
                    if(!nextHopCurrInLight){
                        if(!wallsAndEnemies[nextHop.y][nextHop.x]){
                            model.gameboard.calculateBits(enemy, nextHop);
                            enemy.row = nextHop.y;
                            enemy.col = nextHop.x;
                        }
                    }*/
                    
                    
                    
                    /*
                    //attempt 2: enemy can't move if:
                    //he was in the light before movement or
                    //his next hop will put him in light or
                    //his next hop will put him in a position where light was previously (still fading)
                    //
                    //this ended up being too easy
                     
                    boolean nextHopWasInLight = Utils.isInList(nextHop, model.lastLight);
                    boolean nextHopCurrInLight = Utils.isInList(nextHop, model.currLight);
                    
                    if(!nextHopCurrInLight && !nextHopWasInLight){
                        if(!wallsAndEnemies[nextHop.y][nextHop.x]){
                            model.gameboard.calculateBits(enemy, nextHop);
                            enemy.row = nextHop.y;
                            enemy.col = nextHop.x;
                        }
                    }
                    */
                    
                    //attempt 3: enemy can't move if:
                    //he was in the light before movement or
                    //his next hop will put him in light or
                    //his next hop will put him in a position where light was previously (still fading) 
                    //BUT: he can always move if he is just one hop away from attacking player
                    
                    boolean nextHopWasInLight = Utils.isInList(nextHop, model.lastLight);
                    boolean nextHopCurrInLight = Utils.isInList(nextHop, model.currLight);
                    boolean nextHopIsPlayer = nextHop.equals(model.player.getPos());
                    
                    if((!nextHopCurrInLight && !nextHopWasInLight) || nextHopIsPlayer){
                        if(!wallsAndEnemies[nextHop.y][nextHop.x]){
                            model.gameboard.calculateBits(enemy, nextHop);
                            chupaHopAnimation(enemy, nextHop);
                            enemy.row = nextHop.y;
                            enemy.col = nextHop.x; 
                            
                        }
                    }
                }
            }
        }
    }

    private void chupaHopAnimation(Tile enemy, Point nextHop) {
        if(nextHop.y>enemy.row){
            enemy.dir = Tile.Direction.DOWN;
            enemy.swapAndRestart("ChupaHopFront");
            enemy.stopAtEnd();
        }
        if(nextHop.y<enemy.row){
            enemy.dir = Tile.Direction.UP;
            enemy.swapAndRestart("ChupaHopBack");
            enemy.stopAtEnd();
        }
        if(nextHop.x>enemy.col){
            enemy.dir = Tile.Direction.RIGHT;
            enemy.swapAndRestart("ChupaHopRight");
            enemy.stopAtEnd();
        }
        if(nextHop.x<enemy.col){
            enemy.dir = Tile.Direction.LEFT;
            enemy.swapAndRestart("ChupaHopLeft");
            enemy.stopAtEnd();
        }
        
    }
    
    private void chupaIdleAnimation(Tile enemy) {
        if(enemy.getCurrentAnim() != "ChupaHide"){
            if(enemy.dir == Tile.Direction.DOWN){
                enemy.swapAndResume("ChupaIdleFront");
            }
            if(enemy.dir == Tile.Direction.UP){
                enemy.swapAndResume("ChupaIdleBack");
            }
            if(enemy.dir == Tile.Direction.RIGHT){
                enemy.swapAndResume("ChupaIdleRight");
            }
            if(enemy.dir == Tile.Direction.LEFT){
                enemy.swapAndResume("ChupaIdleLeft");
            }
        }
        enemy.setDirection(Direction.FORWARD);
    }
    
    private void updateChupaLight() {
        for(Tile enemy : model.enemies){
            boolean inLight = Utils.isInList(enemy, model.currLight);
            if(inLight){
                if(enemy.getCurrentAnim() != "ChupaHide"){
                    enemy.swapAndResume("ChupaHide");
                    makeParticles(enemy,0);
                }
                
            }else{
                if(enemy.getCurrentAnim() == "ChupaHide"){
                    makeParticles(enemy,1);
                }
                if(enemy.dir == Tile.Direction.DOWN){
                    enemy.swapAndResume("ChupaIdleFront");
                }
                if(enemy.dir == Tile.Direction.UP){
                    enemy.swapAndResume("ChupaIdleBack");
                }
                if(enemy.dir == Tile.Direction.RIGHT){
                    enemy.swapAndResume("ChupaIdleRight");
                }
                if(enemy.dir == Tile.Direction.LEFT){
                    enemy.swapAndResume("ChupaIdleLeft");
                }
            }
        }

        //clean ducking list of animations no longer on display list
        ArrayList<Tile> toBeDeleted = new ArrayList<Tile>();
        for(Tile duck : model.duckingEnemies){
            if(!model.displayList.get(2).contains(duck)){
                toBeDeleted.add(duck);
            }
        }
        for(Tile duck : toBeDeleted){
            model.duckingEnemies.remove(duck);
        }
    }

    private void makeParticles(Tile enemy, int i) {
        if(i==0){
            Tile duck = new Tile("images/Particles/ChupaDuck");
            duck.row = enemy.row;
            duck.col = enemy.col;
            duck.setOrigin(0, 100);//tile is taller than usual
            duck.removeAtEnd();
            model.gameboard.realPosition(duck);
            model.addChild(duck, 2);
            model.duckingEnemies.add(duck);
            model.diveSound.play();
        }else if(i==1){
            Tile dirt = new Tile("images/Particles/ChupaRise");
            dirt.row = enemy.row;
            dirt.col = enemy.col;
            dirt.removeAtEnd();
            model.gameboard.realPosition(dirt);
            model.addChild(dirt, 2);
            
            //delete unfinished ducking animations
            for(Tile duck : model.duckingEnemies){
                if(model.displayList.get(2).contains(duck)){
                    if(dirt.row == duck.row && dirt.col == duck.col){
                        model.removeChild(duck);
                    }
                }
            }
            
        }
    }

    private void calcCurrentLight() {
        int xstep = 0;
        int ystep = 0;
        if(model.player.dir == Tile.Direction.LEFT){
            xstep = -1;
        }
        if(model.player.dir == Tile.Direction.RIGHT){
            xstep = 1;
        }
        if(model.player.dir == Tile.Direction.UP){
            ystep = -1;
        }
        if(model.player.dir == Tile.Direction.DOWN){
            ystep = 1;
        }
        
        int posX = model.player.col;
        int posY = model.player.row;
        model.currLight.clear();
        for(int i =0;i<Constants.BEAM_LENGTH;i++){
            posX += xstep;
            posY += ystep;
            if(!model.gameboard.inBounds(posY,posX) || model.gameboard.walls[posY][posX]){
                break; //break beam if it hits wall or goes out of bounds
            }else{
                model.currLight.add(new Point(posX,posY));
            }
        }
    } 
    
    private void updateLightAnims() {

        int px = (int) (model.player.x);
        int py = (int) (model.player.y);

        GeneralPath playerMask = new GeneralPath();
        GeneralPath prevLight = new GeneralPath();
        GeneralPath currLight = new GeneralPath();

        //CALCULATE PLAYER MASK
        if(model.player.dir == Tile.Direction.DOWN){
            int x = px;
            int y = py+100;
            playerMask.moveTo(x, y);

            x += Constants.BEAM_LENGTH * model.gameboard.skewX;
            y += Constants.BEAM_LENGTH * 64;
            playerMask.lineTo(x, y);

            x += model.gameboard.width;
            playerMask.lineTo(x, y);

            x = px + model.gameboard.width;
            y = py+100;
            playerMask.lineTo(x, y);
        }else if(model.player.dir == Tile.Direction.UP){
            int x = px+model.gameboard.visualX;
            int y = py+model.gameboard.visualY;
            playerMask.moveTo(x, y);

            x -= Constants.BEAM_LENGTH * model.gameboard.skewX;
            y -= Constants.BEAM_LENGTH * 64;
            playerMask.lineTo(x, y);

            x += model.gameboard.width;
            playerMask.lineTo(x, y);

            x = px +model.gameboard.visualX+ model.gameboard.width;
            y = py+model.gameboard.visualY;
            playerMask.lineTo(x, y);
        }
        else if(model.player.dir == Tile.Direction.LEFT){
            int x = px;
            int y = py+100;
            playerMask.moveTo(x, y);

            x -= Constants.BEAM_LENGTH * model.gameboard.width;
            playerMask.lineTo(x, y);

            x -= model.gameboard.skewX;
            y -= model.gameboard.height;
            playerMask.lineTo(x, y);

            x = px + model.gameboard.visualX;
            y = py+model.gameboard.visualY;
            playerMask.lineTo(x, y);
        }
        else if(model.player.dir == Tile.Direction.RIGHT){
            int x = px+model.gameboard.width;
            int y = py+100;
            playerMask.moveTo(x, y);

            x += Constants.BEAM_LENGTH * model.gameboard.width;
            playerMask.lineTo(x, y);

            x -= model.gameboard.skewX;
            y -= model.gameboard.height;
            playerMask.lineTo(x, y);

            x = px + 100;
            y = py+model.gameboard.visualY;
            playerMask.lineTo(x, y);
        }
        
        //CALCULATE LAST LIGHT
        if(model.lastLight.size()>0){
            int leftBound = model.lastLight.get(0).x;
            int rightBound = model.lastLight.get(0).x;
            int upBound = model.lastLight.get(0).y;
            int downBound = model.lastLight.get(0).y;
            for(Point p : model.lastLight){
                leftBound = Math.min(p.x, leftBound);
                rightBound = Math.max(p.x+1, rightBound);
                upBound = Math.min(p.y, upBound);
                downBound = Math.max(p.y+1, downBound);
            }
            
            Point a = model.gameboard.tileToVisualXY(leftBound, upBound);
            Point b = model.gameboard.tileToVisualXY(rightBound, upBound);
            Point c = model.gameboard.tileToVisualXY(leftBound, downBound);
            Point d = model.gameboard.tileToVisualXY(rightBound, downBound);
            
            prevLight.moveTo(a.x, a.y);
            prevLight.lineTo(b.x, b.y);
            prevLight.lineTo(d.x, d.y);
            prevLight.lineTo(c.x, c.y);
        }

        //CALCULATE CURRENT LIGHT
        if(model.currLight.size()>0){
            int leftBound = model.currLight.get(0).x;
            int rightBound = model.currLight.get(0).x;
            int upBound = model.currLight.get(0).y;
            int downBound = model.currLight.get(0).y;
            for(Point p : model.currLight){
                leftBound = Math.min(p.x, leftBound);
                rightBound = Math.max(p.x+1, rightBound);
                upBound = Math.min(p.y, upBound);
                downBound = Math.max(p.y+1, downBound);
            }
            
            Point a = model.gameboard.tileToVisualXY(leftBound, upBound);
            Point b = model.gameboard.tileToVisualXY(rightBound, upBound);
            Point c = model.gameboard.tileToVisualXY(leftBound, downBound);
            Point d = model.gameboard.tileToVisualXY(rightBound, downBound);
            
            currLight.moveTo(a.x, a.y);
            currLight.lineTo(b.x, b.y);
            currLight.lineTo(d.x, d.y);
            currLight.lineTo(c.x, c.y);
        }

        //DO BOOLEAN COMBINATIONS
        Area areaLastLight = new Area(prevLight);
        Area areaCurrLight = new Area(currLight);
        Area areaPlayerMask= new Area(playerMask);

        if(model.gameState == State.WAIT_FOR_PLAYER){
            model.flashlight = new Area();
            model.flashlight.add(areaPlayerMask);
            model.flashlight.intersect(areaCurrLight);
        }else if(model.gameState == State.MOVE_CHARACTERS){
            model.flashlight = new Area();
            model.flashlight.add(areaLastLight);
            model.flashlight.add(areaCurrLight);
            model.flashlight.intersect(areaPlayerMask);
        }
    }
}
