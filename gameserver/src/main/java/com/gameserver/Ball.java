package com.gameserver;

import java.util.Random;

public class Ball {
    public static final float RADIUS = GameProcess.INITAL_SIZE;
    public static final float SPEED_RATE = 1 / 8f;

    private Vector2 direction;
    private Vector2 position;
    private float speed;

    public Ball() {
        this.direction = new Vector2();
        this.position = new Vector2(GameProcess.X_BOUNDS / 2, GameProcess.Y_BOUNDS / 2);
    }

    public void spawn() {
        Random random = new Random();
        float randomX = (float) Math.cos((random.nextFloat() * (2 * Math.PI)));
        float randomY = (float) Math.sin(random.nextFloat() * (2 * Math.PI));
        direction.x = randomX;
        direction.y = randomY;
        direction.normalize();
    }

    public void increaseSpeed() {
        this.speed += SPEED_RATE;
    }

    public void setDirection(Vector2 direction) {
        this.direction = direction;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Vector2 getPosition() {
        return position;
    }
}
