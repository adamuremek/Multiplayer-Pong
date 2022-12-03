package com.gameserver;

/**
 * @Author Jordan Anodjo
 * 
 * 2 Dimensional Vector class with an X and Y component with simple vector functions
 */
public class Vector2 {

    public float x;
    public float y;

    public static final Vector2 X_AXIS_VECTOR = new Vector2(1, 0);
    public static final Vector2 Y_AXIS_VECTOR = new Vector2(0, 1);
    public static final Vector2 NEGATIVE_VECTOR = new Vector2(-1, -1);

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

    public static void rotate(Vector2 vector, float angle) {
        float x = (float) (vector.x * Math.cos(angle) - vector.y * Math.sin(angle));
        float y = (float) (vector.x * Math.sin(angle) + vector.y * Math.cos(angle));
        vector.x = x;
        vector.y = y;

    }

    public static float getAngle(Vector2 v1, Vector2 v2) {
        return (float) Math.acos(dot(v1, v2) / (magnitude(v1) * magnitude(v2)));
    }

    public static void normalize(Vector2 vector) {
        float magnitude = magnitude(vector);
        vector.x /= magnitude;
        vector.y /= magnitude;
    }

    public static Vector2 mulitply(Vector2 vector, float num) {
        return new Vector2(vector.x * num, vector.y * num);
    }

    public static float magnitude(Vector2 vector) {
        return (float) (Math.sqrt(vector.x * vector.x + vector.y * vector.y));
    }

    public static void reflectVector(Vector2 vector, Vector2 axis){
        float currentAngle = dot(vector, axis);
        currentAngle = currentAngle >= 180 ? currentAngle - 180 : currentAngle;

        float newAngle = 2 * (180 - currentAngle) + currentAngle;
        newAngle = (float) Math.toRadians(newAngle);
        rotate(vector, newAngle);
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
        return (int) (this.x / magnitude(this) + this.y / magnitude(this));
    }

    @Override
    public String toString() {
        return  String.format("(%.3f, %.3f)", this.x, this.y);
    }
}
