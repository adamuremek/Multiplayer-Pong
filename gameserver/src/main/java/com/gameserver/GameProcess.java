package com.gameserver;

//TODO: Jordan
public class GameProcess extends Thread {
    private static final float X_BOUNDS = 640;
    private static final float Y_BOUNDS = 480;
    private static final float INITAL_SIZE = 5;
    private static final float PADDLE_SIZE_X = INITAL_SIZE * 1.5f;
    private static final float PADDLE_SIZE_Y = INITAL_SIZE * 7;
    private static final float BALL_RADIUS = INITAL_SIZE;

    private GameData gameData;

    public GameProcess(GameData gameData) {
        this.gameData = gameData;
        this.run();
    }

    public void stepPhysics() {
        GameState gameState = new GameState();

        /* Stream input will update this */

        if (gameData.ballVecPos.x - BALL_RADIUS <= 0) {
            gameData.p1score += 1;
            // TODO gameData.reset()

        } else if (gameData.ballVecPos.x + BALL_RADIUS >= X_BOUNDS) {
            gameData.p2score += 1;
            // TODO gameData.reset()

        }

        if (gameData.ballVecPos.y - BALL_RADIUS <= 0) {
            //TODO move ball
            System.out.println("wall");

        } else if (gameData.ballVecPos.y + BALL_RADIUS >= Y_BOUNDS) {
            System.out.println("wall");
            //TODO move ball

        }

    }

    public void tick() {
        stepPhysics();
    }

    @Override
    public void run() {
        tick();
    }
}
