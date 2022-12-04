package com.gameserver;

/**
 * @author Jordan Anodjo
 */
public class Paddle {

    private static final float INITAL_SIZE = 5;
    private static final float X_STANDOFF = 30;
    public static final float PADDLE_WIDTH = INITAL_SIZE * 1.5f;
    public static final float PADDLE_HEIGHT = INITAL_SIZE * 7;

    public Vector2 center;
    public Vector2 focalPoint;

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

    public Paddle(Side side) {
        if(side == Side.LEFT){
            this.center = new Vector2(X_STANDOFF, GameProcess.Y_BOUNDS / 2);
            this.focalPoint = new Vector2(0, center.y);
        }
        else{
            this.center = new Vector2(GameProcess.X_BOUNDS - X_STANDOFF, GameProcess.Y_BOUNDS / 2);
            this.focalPoint = new Vector2(640, center.y);
        }
    }

    public void movePaddle(float y) {
        this.center.y = y;
        this.focalPoint.y = y;
    }
}