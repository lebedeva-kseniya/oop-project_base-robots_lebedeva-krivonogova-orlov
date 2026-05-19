package gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import log.LogEntry;
import log.LogLevel;

public class RobotModel {
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;
    private final PropertyChangeSupport propertyChangeSupport;

    public RobotModel() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
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

    public double getRobotPositionX() { return m_robotPositionX; }
    public double getRobotPositionY() { return m_robotPositionY; }
    public double getRobotDirection() { return m_robotDirection; }

    public LogEntry getCoordsLogEntry() {
        return new LogEntry(LogLevel.Debug, "log.robot.coords",
                getRobotPositionX(),
                getRobotPositionY(),
                Math.toDegrees(getRobotDirection()));
    }
}