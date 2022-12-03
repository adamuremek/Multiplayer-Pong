package com.gameserver;
import java.util.concurrent.TimeUnit;

/**
 * @Author Jordan Anodjo
 * 
 * Processes the physics that the game will run under
 */
public class GameProcess extends Thread {
    public static final float X_BOUNDS = 640;
    public static final float Y_BOUNDS = 480;
    public static final float INITAL_SIZE = 5;

    public GameProcess() {
        this.run();
    }

    public void stepPhysics() {

        // Server will update ball position
        Ball ball = GameServer.gameData.ball;
        Paddle paddle1 = GameServer.gameData.paddle1;
        Paddle paddle2 = GameServer.gameData.paddle2;

        if (!ball.exists()) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ball.spawn();
        }

        Vector2 ballPosition = ball.position;

        if (ballPosition.x + Ball.RADIUS >= X_BOUNDS) {
            GameServer.gameData.player1Score += 1;
            ball.despawn();
        } else if (ballPosition.x - Ball.RADIUS <= 0) {
            GameServer.gameData.player2Score += 1;
            ball.despawn();
        }

        if (ballPosition.y + Ball.RADIUS >= Y_BOUNDS || ballPosition.y - Ball.RADIUS <= 0)
            Vector2.reflectVector(ballPosition, Vector2.X_AXIS_VECTOR);

        Vector2 paddle1Collision = paddle1.getCollision(ball);
        Vector2 paddle2Collision = paddle2.getCollision(ball);

        if (paddle1Collision != Vector2.NEGATIVE_VECTOR){
            paddle1.reboundOfBall(ball, paddle1Collision);
            ball.increaseSpeed();
        }
        else if (paddle2Collision != Vector2.NEGATIVE_VECTOR){
            paddle2.reboundOfBall(ball, paddle2Collision);
            ball.increaseSpeed();
        }

        ball.position = Vector2.mulitply(ball.direction, ball.getSpeed());

    }

    public void tick() {
        
        stepPhysics();
        GameServer.cl.updateClients();
    }

    @Override
    public void run() {
        tick();
    }
}
