package io.github.xanderstuff.ultimatehud.util;

public class Vector2d {
    public double x;
    public double y;

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d copy() {
        return new Vector2d(this.x, this.y);
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void multiply(double amount) {
        this.x *= amount;
        this.y *= amount;
    }

    public void add(Vector2d otherVector) {
        this.x += otherVector.x;
        this.y += otherVector.y;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

}
