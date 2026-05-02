package gui;

import java.util.List;
import java.util.ArrayList;
import java.awt.Point;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class RobotController implements Runnable {
    private final RobotModel m_model;

    private final ConcurrentLinkedQueue<Point> m_targetQueue = new ConcurrentLinkedQueue<>();
    private final Object m_monitor = new Object();

    private final List<Point> m_obstacles = Collections.synchronizedList(new ArrayList<>());

    private final PropertyChangeSupport m_changeSupport = new PropertyChangeSupport(this);

    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.005;
    private static final double TARGET_REACHED_RADIUS = 2.0;
    private static final double OBSTACLE_COLLISION_RADIUS = 20.0;

    private static final double SENSOR_VIEW_ANGLE = Math.PI / 3;
    private static final double ANGLE_ACCURACY_THRESHOLD = 0.05;
    private static final double LOOK_AHEAD_DISTANCE_PADDING = 30.0;
    private static final double TICK_DURATION_MS = 10.0;

    public RobotController(RobotModel model) {
        this.m_model = model;
    }

    public void addTarget(Point p) {
        m_targetQueue.add(p);
        synchronized (m_monitor) {
            m_monitor.notifyAll();
        }
    }

    public void notifyObstacleChanged() {
        synchronized (m_monitor) {
            m_monitor.notifyAll();
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        m_changeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            Point currentTarget = m_targetQueue.peek();

            if (currentTarget != null) {
                if (distance(m_model.getRobotPositionX(), m_model.getRobotPositionY(),
                        currentTarget.x, currentTarget.y) < TARGET_REACHED_RADIUS) {
                    m_targetQueue.poll();
                    continue;
                }
                step(currentTarget);
            } else {
                synchronized (m_monitor) {
                    try {
                        m_monitor.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private boolean isPathBlocked(Point target) {
        double rx = m_model.getRobotPositionX();
        double ry = m_model.getRobotPositionY();
        double rd = m_model.getRobotDirection();

        double distToTarget = distance(rx, ry, target.x, target.y);

        double lookAheadDistance = OBSTACLE_COLLISION_RADIUS + LOOK_AHEAD_DISTANCE_PADDING;

        List<Point> obstacles = getObstacles();

        for (Point obs : obstacles) {
            double dx = obs.x - rx;
            double dy = obs.y - ry;
            double distToCenter = Math.sqrt(dx * dx + dy * dy);

            if (distToCenter < OBSTACLE_COLLISION_RADIUS) {
                return true;
            }

            double angleToObs = Math.atan2(dy, dx);
            double angleDiff = asNormalizedRadians(angleToObs - rd);
            if (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;

            if (Math.abs(angleDiff) < SENSOR_VIEW_ANGLE) {

                if (distToCenter < distToTarget) {

                    double distanceToLine = Math.abs(distToCenter * Math.sin(angleDiff));

                    if (distanceToLine < OBSTACLE_COLLISION_RADIUS && distToCenter < lookAheadDistance) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void step(Point target) {
        double robotX = m_model.getRobotPositionX();
        double robotY = m_model.getRobotPositionY();
        double robotDirection = m_model.getRobotDirection();

        double angleToTarget = angleTo(robotX, robotY, target.x, target.y);
        double angleDiff = asNormalizedRadians(angleToTarget - robotDirection);
        if (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;

        double angularVelocity = 0;
        if (Math.abs(angleDiff) > ANGLE_ACCURACY_THRESHOLD) {
            angularVelocity = (angleDiff > 0) ? MAX_ANGULAR_VELOCITY : -MAX_ANGULAR_VELOCITY;
        }

        if (isPathBlocked(target)) {
            // Робот останавливается (скорость 0), но продолжает вращаться к цели
            moveRobot(0, angularVelocity, TICK_DURATION_MS);
        } else {
            moveRobot(MAX_VELOCITY, angularVelocity, TICK_DURATION_MS);
        }
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        double direction = m_model.getRobotDirection();
        double newDirection = asNormalizedRadians(direction + angularVelocity * duration);

        double newX = m_model.getRobotPositionX() + velocity * duration * Math.cos(newDirection);
        double newY = m_model.getRobotPositionY() + velocity * duration * Math.sin(newDirection);

        m_model.setRobotPosition(newX, newY, newDirection);
    }

    public List<Point> getObstacles() {
        return new ArrayList<>(m_obstacles);
    }

    public void addObstacle(Point p) {
        m_obstacles.add(p);
        m_changeSupport.firePropertyChange("obstacles", null, getObstacles());
    }

    public boolean removeObstacleAt(Point p) {
        synchronized (m_obstacles) {
            boolean removed = m_obstacles.removeIf(obs -> p.distance(obs) < 20);

            if (removed) {
                m_changeSupport.firePropertyChange("obstacles", null, getObstacles());
            }
            return removed;
        }
    }

    public List<Point> getTargets() {
        return new ArrayList<>(m_targetQueue);
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        return asNormalizedRadians(Math.atan2(toY - fromY, toX - fromX));
    }

    private static double asNormalizedRadians(double angle) {
        double normalized = angle % (2 * Math.PI);
        if (normalized < 0) {
            normalized += 2 * Math.PI;
        }
        return normalized;
    }
}