package com.gameserver;

//TODO: Jordan
public class GameProcess extends Thread {
    public static final float X_BOUNDS = 640;
    public static final float Y_BOUNDS = 480;
    public static final float INITAL_SIZE = 5;
    

    private GameData gameData;

    public GameProcess(GameData gameData) {
        this.gameData = gameData;
        this.run();
    }

    private void movePaddle() {
        
    }


    public void stepPhysics() {

        



    }

    public void tick() {
        stepPhysics();
    }

    @Override
    public void run() {
        tick();
    }
}
