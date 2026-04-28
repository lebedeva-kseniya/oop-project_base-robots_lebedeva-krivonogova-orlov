package gui;

import java.util.List;
import java.util.ArrayList;
import java.awt.Point;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RobotController implements Runnable {
    private final RobotModel m_model;

    private final ConcurrentLinkedQueue<Point> m_targetQueue = new ConcurrentLinkedQueue<>();
    private final Object m_monitor = new Object();

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.005;
    private static final double TARGET_REACHED_RADIUS = 2.0;

    private static final double OBSTACLE_COLLISION_RADIUS = 40.0;

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

        double lookAheadDistance = OBSTACLE_COLLISION_RADIUS + 30.0;

        List<Point> obstacles = m_model.getObstacles();

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

            if (Math.abs(angleDiff) < Math.PI / 3) {
                double distanceToLine = Math.abs(distToCenter * Math.sin(angleDiff));

                if (distanceToLine < OBSTACLE_COLLISION_RADIUS) {
                    if (distToCenter < lookAheadDistance) {
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
        if (Math.abs(angleDiff) > 0.05) {
            angularVelocity = (angleDiff > 0) ? maxAngularVelocity : -maxAngularVelocity;
        }

        if (isPathBlocked(target)) {
            moveRobot(0, angularVelocity, 10);
        } else {
            moveRobot(maxVelocity, angularVelocity, 10);
        }
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        double direction = m_model.getRobotDirection();
        double newDirection = asNormalizedRadians(direction + angularVelocity * duration);

        double newX = m_model.getRobotPositionX() + velocity * duration * Math.cos(newDirection);
        double newY = m_model.getRobotPositionY() + velocity * duration * Math.sin(newDirection);

        m_model.setRobotPosition(newX, newY, newDirection);
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
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
}