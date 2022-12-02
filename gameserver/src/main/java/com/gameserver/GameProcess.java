package com.gameserver;

//TODO: Jordan
public class GameProcess extends Thread {
    public static final float X_BOUNDS = 640;
    public static final float Y_BOUNDS = 480;
    private static final float INITAL_SIZE = 5;
    private static final float PADDLE_SIZE_X = INITAL_SIZE * 1.5f;
    private static final float PADDLE_SIZE_Y = INITAL_SIZE * 7;
    private static final float BALL_RADIUS = INITAL_SIZE;
    
    private Vector2 foci;
    private GameData gameData;

    public GameProcess(GameData gameData) {
        this.gameData = gameData;
        
        this.run();
    }

    public void stepPhysics() {
        GameState gameState = new GameState();

        

    }

    public void calcFocal() {
        


        
        foci = new Vector2();
    }

    public void tick() {
        stepPhysics();
    }

    @Override
    public void run() {
        tick();
    }
}
