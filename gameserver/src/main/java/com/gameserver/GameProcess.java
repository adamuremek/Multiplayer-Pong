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
        Vector2 p1Velocity = gameData.getP1Velocity();
        Vector2 p2Velocity = gameData.getP2Velocity();
        Vector2 ballVelocity = gameData.getBallVelocity();

        float[] ballPos =  gameData.getBallPos();
        float[] p1Pos = gameData.getP1Pos();
        float[] p2Pos = gameData.getP2Pos();

        
        
        



    }

    public void tick(){
        stepPhysics();
    }

    @Override
    public void run(){
        
    }
}
