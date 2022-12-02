package com.gameserver;

public class Paddle {

    private static final float INITAL_SIZE = 5;
    private static final float X_STANDOFF = 30;
    private static final float PADDLE_SIZE_X = INITAL_SIZE * 1.5f;
    private static final float PADDLE_SIZE_Y = INITAL_SIZE * 7;
    private static final float MOVE_RATE = 0.5f;
    private Side side;

    private Vector2 center;
    private Vector2 a;
    private Vector2 b;
    private Vector2 c;
    private Vector2 d;

    private Vector2 focalPoint;

    enum Side {
        LEFT, RIGHT
    }

    /**
     * a----------b
     * | |
     * | |
     * | center |
     * | |
     * | |
     * c----------d
     * 
     * @param center
     */
    private void constructProperties(Vector2 center) {
        a = new Vector2(center.x - PADDLE_SIZE_X, center.y + PADDLE_SIZE_Y);
        b = new Vector2(center.x + PADDLE_SIZE_X, center.y + PADDLE_SIZE_Y);
        c = new Vector2(center.x + PADDLE_SIZE_X, center.y - PADDLE_SIZE_Y);
        d = new Vector2(center.x - PADDLE_SIZE_X, center.y - PADDLE_SIZE_Y);

        float x;
        if (this.side == Side.LEFT)
            x = -(-center.y + (this.b.y - this.b.x));

        else if (this.side == Side.RIGHT)
            x = (-center.y + (this.b.y + this.b.x));
        else
            return;

        focalPoint = new Vector2(x, center.y);
    }

    public Paddle(Side side) {
        this.side = side;

        if (this.side == Side.LEFT)
            center = new Vector2(X_STANDOFF, GameProcess.Y_BOUNDS / 2);
        else if (this.side == Side.RIGHT)
            center = new Vector2(GameProcess.X_BOUNDS - X_STANDOFF, GameProcess.Y_BOUNDS / 2);

        constructProperties(center);

    }

    public void movePaddle() {

    }

}
