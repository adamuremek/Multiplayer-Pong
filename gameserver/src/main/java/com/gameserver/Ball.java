package com.gameserver;

import java.util.Random;

/**
 * @author Jordan Anodjo
 * 
 * The ball the program will be using
 */
public class Ball {
    public static final float RADIUS = GameProcess.INITAL_SIZE;
    public static final float SPEED_RATE = 1 / 8f;

    public Vector2 direction;
    public Vector2 position;

    public Random random = new Random();
    private float speed;
    private boolean exist;

    public Ball() {
        this.direction = new Vector2();
        this.position = new Vector2(GameProcess.X_BOUNDS / 2, GameProcess.Y_BOUNDS / 2);
        exist = false;
    }

    public void spawn() {
        direction.x = (float) Math.cos((random.nextFloat() * (2 * Math.PI)));
        direction.y = (float) Math.sin(random.nextFloat() * (2 * Math.PI));
        Vector2.normalize(direction);
        speed = 1;
        exist = true;
    }

    public void increaseSpeed() {
        this.speed += SPEED_RATE;
    }

    public float getSpeed() {
        return speed;
    }


    public boolean exists() {
        return exist;
    }

    public void despawn() {
        exist = false;
    }

}
