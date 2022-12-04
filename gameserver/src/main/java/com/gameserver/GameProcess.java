package com.gameserver;

/**
 * @Author Jordan Anodjo
 * 
 * Processes the physics that the game will run under
 */
public class GameProcess extends Thread {
    public static final float X_BOUNDS = 640f;
    public static final float Y_BOUNDS = 480f;
    private boolean isActive;
    private long lastTime = System.nanoTime();
    private int deltaTime = 0;

    public GameProcess() {
        isActive = true;
        this.run();
    }

    public void endGameProcess(){
        isActive = false;
    }

    public boolean collidedWithTopOrBottom(Ball ball, float yBound){
        if(yBound > 0)
            return ball.position.y + Ball.RADIUS >= yBound;
        else
            return  ball.position.y - Ball.RADIUS <= yBound;
    }

    public boolean collidedWithLeftOrRight(Ball ball, float xBound){
        if(xBound > 0)
            return ball.position.x + Ball.RADIUS >= xBound;
        else
            return  ball.position.x - Ball.RADIUS <= xBound;
    }

    public boolean collidedWithPaddle(Ball ball, Paddle paddle){
        //Temporary variables to set edges for testing
        float testX = ball.position.x;
        float testY = ball.position.y;

        //Calculate closest edge
        //Test left edge
        if(ball.position.x < paddle.center.x - Paddle.PADDLE_WIDTH)
            testX = paddle.center.x - Paddle.PADDLE_WIDTH;
        //Test right edge
        else if(ball.position.x >  paddle.center.x + Paddle.PADDLE_WIDTH)
            testX = paddle.center.x + Paddle.PADDLE_WIDTH;
        //Test top edge
        if(ball.position.y < paddle.center.y - Paddle.PADDLE_HEIGHT)
            testY = paddle.center.y;
        //Test bootom edge
        else if(ball.position.y > paddle.center.y + Paddle.PADDLE_HEIGHT)
            testY = paddle.center.y + Paddle.PADDLE_HEIGHT;


        //Get distance from closest edges
        float distX = ball.position.x - testX;
        float distY = ball.position.y - testY;
        float distance = (float)Math.sqrt((distX * distX) + (distY * distY));
            
        return distance <= Ball.RADIUS;
    }

    public void stepPhysics() {

        // Server will update ball position
        Ball ball = GameServer.gameData.ball;
        Paddle paddle1 = GameServer.gameData.paddle1;
        Paddle paddle2 = GameServer.gameData.paddle2;

        if (ball.canStart() && !ball.hasStarted && GameServer.serverInfo.isFull)
            ball.start();
        else if(!ball.hasStarted && GameServer.serverInfo.isFull){
            ball.tickCooldown(deltaTime);
            return;
        } else if (!GameServer.serverInfo.isFull)
            ball.reset();

        //Score and reset ball on right goal
        if (collidedWithLeftOrRight(ball, X_BOUNDS)) {
            GameServer.gameData.player1Score += 1;
            ball.reset();
            return;
        } 

         //Score and reset ball on right goal
        if (collidedWithLeftOrRight(ball, 0)) {
            GameServer.gameData.player2Score += 1;
            ball.reset();
            return;
        }

        //Bounce off of top bound
        if(collidedWithTopOrBottom(ball, Y_BOUNDS))
            ball.direction =  Vector2.reflectVector(ball.direction, Vector2.DOWN);
    
        //Bounce off of bottom bound
        if(collidedWithTopOrBottom(ball, 0f))
            ball.direction = Vector2.reflectVector(ball.direction, Vector2.UP);
        
    
        //Hit Left paddle
        if(collidedWithPaddle(ball, paddle1)){
            ball.direction = Vector2.normalize(Vector2.sub(ball.position, paddle1.focalPoint));
            ball.increaseSpeed();
        }

        //Hit right paddle
        if(collidedWithPaddle(ball, paddle2)){
            ball.direction = Vector2.normalize(Vector2.sub(ball.position, paddle2.focalPoint));
            ball.increaseSpeed();
        }


        ball.position =  Vector2.add(ball.position, Vector2.scalarMulitply(ball.direction, ball.getSpeed()));
        //System.out.println(ball.position);
    }

    public void tick() {
        stepPhysics();
        GameServer.cl.updateClients();
        //Calculate deltaTime
        long currentTime = System.nanoTime();
        deltaTime = (int)((currentTime - lastTime) / 1000000);
        lastTime = currentTime;
    }

    @Override
    public void run() {
        while(isActive){
            try {
                tick();
                Thread.sleep(10);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}