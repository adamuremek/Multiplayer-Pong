package com.gameserver;

//TODO: Jordan
public class GameProcess extends Thread{
    private static final float X_BOUNDS = 640;
    private static final float Y_BOUNDS = 480;
    private static final float INITAL_SIZE = 5;
    private static final float PADDLE_SIZE_X = INITAL_SIZE * 1.5f;
    private static final float PADDLE_SIZE_Y = INITAL_SIZE * 7;
    private static final float BALL_RADIUS = INITAL_SIZE;

    

    private GameData gameData;
    public GameProcess(GameData gameData){
        this.gameData = gameData;
    }

    public void stepPhysics(){
        Vector2 p1VecPos = gameData.getP1VecPos();
        Vector2 p2VecPos = gameData.getP2VecPos();
        Vector2 ballVecPos = gameData.getBallVecPos();

        Vector2 ballVelDir = gameData.getBallVelDir();

        

        
        
        



    }

    public void tick(){
        stepPhysics();
    }

    @Override
    public void run(){
        
    }
}
