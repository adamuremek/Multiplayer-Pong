package com.gameserver;

//TODO: Jordan
public class Vector2 {
    float x;
    float y;

    public Vector2() {
        x = 0;
        y = 0;
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float dot(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    public Vector2 rotate(float angle) {

        float x = (float) (this.x * Math.cos(angle) - this.y * Math.sin(angle));
        float y = (float) (this.y * Math.sin(angle) + this.y * Math.cos(angle));
        return new Vector2(x, y);
    }
}
