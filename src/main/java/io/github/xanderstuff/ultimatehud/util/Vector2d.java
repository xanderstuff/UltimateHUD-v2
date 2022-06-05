package io.github.xanderstuff.ultimatehud.util;

public class Vector2d {
    public double x;
    public double y;

    public Vector2d() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void add(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public Vector2d copy() {
        return new Vector2d(this.x, this.y);
    }

    public Vector2d multiply(double amount) {
        return new Vector2d(this.x * amount, this.y * amount);
    }

    public Vector2d multiplyEntrywise(Vector2d otherVector) {
        return new Vector2d(this.x * otherVector.x, this.y * otherVector.y);
    }

    public Vector2d add(Vector2d otherVector) {
        return new Vector2d(this.x + otherVector.x, this.y + otherVector.y);
    }

    public Vector2d subtract(Vector2d otherVector) {
        return new Vector2d(this.x - otherVector.x, this.y - otherVector.y);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

}
