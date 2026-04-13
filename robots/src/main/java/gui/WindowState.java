package gui;

public class WindowState implements java.io.Serializable {
    private static final long serialVersionUID = 2L;
    private Rectangle bounds;
    private boolean minimized;
    private int layer;

    public WindowState(java.awt.Rectangle bounds, boolean minimized, int layer) {
        this.bounds = new Rectangle(bounds);
        this.minimized = minimized;
        this.layer = layer;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isMinimized() {
        return minimized;
    }

    public int getLayer() {
        return layer;
    }
}