package com.gameserver;

import java.util.Random;

/**
 * @author Jordan Anodjo
 * 
 * The ball the program will be using
 */
public class Ball {
    public static final float RADIUS = 5f;
    public static final float SPEED_RATE = 0.5f;
    public static final float DEFAULT_SPEED = 2f;
    private final int COOLDOWN_TIME_MS = 3000;
    public Vector2 direction;
    public Vector2 position;

    public Random random = new Random();
    private float speed;
    private int cooldown;
    public boolean hasStarted;

    public Ball() {
        this.direction = new Vector2();
        this.position = new Vector2(GameProcess.X_BOUNDS / 2, GameProcess.Y_BOUNDS / 2);
        this.cooldown = COOLDOWN_TIME_MS;
        this.hasStarted = false;
    }

    public void start() {
        direction.x = (float) Math.cos((random.nextFloat() * (2 * Math.PI)));
        direction.y = (float) Math.sin(random.nextFloat() * (2 * Math.PI));
        Vector2.normalize(direction);
        speed = DEFAULT_SPEED;
        this.hasStarted = true;
    }

    public void increaseSpeed() {
        this.speed += SPEED_RATE;
    }

    public float getSpeed() {
        return speed;
    }

    public void tickCooldown(int time){
        cooldown -= time;

        if(cooldown < 0)
            cooldown = 0;
    }

    public boolean canStart() {
        return cooldown == 0;
    }

    public void reset() {
        position = new Vector2(GameProcess.X_BOUNDS / 2, GameProcess.Y_BOUNDS / 2);
        cooldown = COOLDOWN_TIME_MS;
        this.hasStarted = false;
    }

}