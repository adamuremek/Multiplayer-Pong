package com.gameserver;

//TODO: Jordan
public class Vector2 {
    public float x;
    public float y;

    public Vector2() {
        x = 0;
        y = 0;
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static float dot(Vector2 self, Vector2 other) {
        return self.x * other.x + self.y * other.y;
    }

    public Vector2 rotate(float angle) {
        float x = (float) (this.x * Math.cos(angle) - this.y * Math.sin(angle));
        float y = (float) (this.y * Math.sin(angle) + this.y * Math.cos(angle));
        return new Vector2(x, y);
    }

    public static float getAngle(Vector2 v1, Vector2 v2) {
        return (float) Math.acos(dot(v1, v2) / (magnitude(v1) * magnitude(v2)));
    }

    public void normalize() {
        float magnitude = magnitude(this);
        this.x /= magnitude;
        this.y /= magnitude;
    }

    public static float magnitude(Vector2 vector) {
        return (float) (Math.sqrt(vector.x * vector.x + vector.y * vector.y));
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Vector2))
            return false;

        Vector2 other = (Vector2) obj;

        return other.x == this.x && other.y == this.y;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
