package gui;

import java.io.Serializable;

public class Rectangle implements Serializable {
    private static final long serialVersionUID = 1L;

    public int x;
    public int y;
    public int width;
    public int height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle(java.awt.Rectangle awtRect) {
        this(awtRect.x, awtRect.y, awtRect.width, awtRect.height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Rectangle)) return false;

        Rectangle r = (Rectangle) obj;
        return (x == r.x) && (y == r.y) && (width == r.width) && (height == r.height);
    }

    @Override
    public int hashCode() {
        return x ^ y ^ width ^ height;
    }
}