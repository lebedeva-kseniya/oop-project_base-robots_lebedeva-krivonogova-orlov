package gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class RobotModel {
    private int m_windowWidth = 800;
    private int m_windowHeight = 600;

    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private final List<Point> m_obstacles = Collections.synchronizedList(new ArrayList<>());

    private final PropertyChangeSupport propertyChangeSupport;

    public RobotModel() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void setRobotPosition(double x, double y, double direction) {
        double oldX = m_robotPositionX;
        double oldY = m_robotPositionY;
        double oldDir = m_robotDirection;

        m_robotPositionX = x;
        m_robotPositionY = y;
        m_robotDirection = direction;

        propertyChangeSupport.firePropertyChange("positionX", oldX, x);
        propertyChangeSupport.firePropertyChange("positionY", oldY, y);
        propertyChangeSupport.firePropertyChange("direction", oldDir, direction);
    }

    public void setWindowSize(int width, int height) {
        this.m_windowWidth = width;
        this.m_windowHeight = height;
    }

    public double getRobotPositionX() {
        return m_robotPositionX;
    }

    public double getRobotPositionY() {
        return m_robotPositionY;
    }

    public double getRobotDirection() {
        return m_robotDirection;
    }

    public String getCoordsLogMessage() {
        return LocalizationSupport.format("log.robot.coords",
                getRobotPositionX(),
                getRobotPositionY(),
                Math.toDegrees(getRobotDirection()));
    }
}