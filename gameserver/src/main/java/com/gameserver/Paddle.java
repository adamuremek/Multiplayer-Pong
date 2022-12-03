package com.gameserver;

public class Paddle {

    private static final float INITAL_SIZE = 5;
    private static final float X_STANDOFF = 30;
    private static final float PADDLE_WIDTH = INITAL_SIZE * 1.5f;
    private static final float PADDLE_HEIGHT = INITAL_SIZE * 7;

    private Side side;

    private Vector2 center;
    private Vector2 a;
    private Vector2 b;
    private Vector2 c;
    private Vector2 d;

    private Vector2 focalPoint;

    enum Side {
        LEFT(0),
        RIGHT(1);

        private int idx;

        private Side(int idx) {
            this.idx = idx;
        }

        public int getIdx() {
            return this.idx;
        }
    }

    /**
     * a----------b
     * |..........|
     * |..........|
     * |..center..|
     * |..........|
     * |..........|
     * c----------d
     * 
     * @param side
     */
    public Paddle(Side side) {
        this.side = side;

        this.center = new Vector2(GameProcess.X_BOUNDS - X_STANDOFF * this.side.getIdx(), GameProcess.Y_BOUNDS / 2);
        this.a = new Vector2(center.x - PADDLE_WIDTH, center.y - PADDLE_HEIGHT);
        this.b = new Vector2(center.x + PADDLE_WIDTH, center.y - PADDLE_HEIGHT);
        this.c = new Vector2(center.x - PADDLE_WIDTH, center.y + PADDLE_HEIGHT);
        this.d = new Vector2(center.x + PADDLE_WIDTH, center.y + PADDLE_HEIGHT);

        float x = center.x;
        if (this.side == Side.LEFT)
            x = -(-center.y + (this.b.y - this.b.x));
        else if (this.side == Side.RIGHT)
            x = (-center.y + (this.a.y + this.a.x));

        this.focalPoint = new Vector2(x, center.y);

    }

    public void movePaddle(float y) {
        if (y - PADDLE_HEIGHT > 0 && y + PADDLE_HEIGHT < GameProcess.Y_BOUNDS) {
            this.center.y = y;
            this.a.y = y;
            this.b.y = y;
            this.c.y = y;
            this.d.y = y;
            this.focalPoint.y = y;
        }
    }

    public Vector2 isCollid(Ball ball) {

        Vector2 ballPosition = ball.getPosition();

        // Default closests X and Y to be the center of the ball
        float closestX = ballPosition.x;
        float closestY = ballPosition.y;

        // Find the closest X
        if (ballPosition.x < this.a.x)
            closestX = this.a.x;
        else if (ballPosition.x > this.a.x + PADDLE_WIDTH)
            closestX = this.a.x + PADDLE_WIDTH;

        // Find the closest Y
        if (ballPosition.y < this.a.y)
            closestY = this.a.y;
        else if (ballPosition.y > this.a.y + PADDLE_HEIGHT)
            closestY = this.a.y + PADDLE_HEIGHT;

        // Calculate the distance from closestX and closestY to the ball's <x,y>
        float distanceX = ballPosition.x - closestX;
        float distanceY = ballPosition.y - closestY;
        
        // If the distance is less than the ball radius there is a collision
        float distance = (float) Math.sqrt((distanceX * distanceX) + (distanceY * distanceY));

        // Return the vector of collision if it DNE return an impossible value for this program 
        return distance <= Ball.RADIUS ? new Vector2(closestX, closestY) : new Vector2(-1, -1);
    }

}
