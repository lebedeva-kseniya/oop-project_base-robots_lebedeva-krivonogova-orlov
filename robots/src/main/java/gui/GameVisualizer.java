package gui;

import log.Logger;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GameVisualizer extends JPanel implements PropertyChangeListener {
    private final RobotModel m_model;
    private final RobotController m_controller;
    private final Timer m_timer = initTimer();

    public static final int MIN_SCALE_PCT = 10;   // 0.1x
    public static final int MAX_SCALE_PCT = 400;  // 4.0x
    public static final int DEFAULT_SCALE_PCT = 100; // 1.0x

    private double m_scale = 1.0;
    private double m_offsetX = 0.0;
    private double m_offsetY = 0.0;
    private Point m_dragStart;

    private java.util.function.Consumer<Integer> m_scaleChangeListener;

    private static Timer initTimer() {
        return new Timer("events generator", true);
    }

    public GameVisualizer(RobotModel model, RobotController controller) {
        this.m_model = model;
        this.m_controller = controller;
        this.m_controller.addPropertyChangeListener(this);

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 16);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    m_dragStart = e.getPoint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && m_dragStart != null) {
                    if (e.getPoint().distance(m_dragStart) < 5) {
                        Point worldPoint = screenToWorld(e.getPoint());
                        m_controller.addTarget(worldPoint);
                        Logger.debug("log.target_set", worldPoint.x, worldPoint.y);
                    }
                    m_dragStart = null;
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    Point worldPoint = screenToWorld(e.getPoint());
                    if (m_controller.removeObstacleAt(worldPoint)) {
                        Logger.debug("log.obstacle_removed", worldPoint.x, worldPoint.y);
                    } else {
                        m_controller.addObstacle(worldPoint);
                        Logger.debug("log.obstacle_added", worldPoint.x, worldPoint.y);
                    }
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && m_dragStart != null) {
                    Point currentPoint = e.getPoint();
                    double dx = currentPoint.x - m_dragStart.x;
                    double dy = currentPoint.y - m_dragStart.y;

                    m_offsetX += dx;
                    m_offsetY += dy;

                    m_dragStart = currentPoint;
                    repaint();
                }
            }
        });

        addMouseWheelListener(e -> {
            double zoomFactor = 1.1;
            double oldScale = m_scale;

            if (e.getWheelRotation() < 0) {
                m_scale *= zoomFactor;
            } else {
                m_scale /= zoomFactor;
            }

            m_scale = Math.max(MIN_SCALE_PCT / 100.0, Math.min(m_scale, MAX_SCALE_PCT / 100.0));

            Point mousePoint = e.getPoint();
            m_offsetX = mousePoint.x - (mousePoint.x - m_offsetX) * (m_scale / oldScale);
            m_offsetY = mousePoint.y - (mousePoint.y - m_offsetY) * (m_scale / oldScale);

            if (m_scaleChangeListener != null) {
                m_scaleChangeListener.accept((int) Math.round(m_scale * 100));
            }

            repaint();
        });

        setDoubleBuffered(true);
    }

    public void setScaleChangeListener(java.util.function.Consumer<Integer> listener) {
        this.m_scaleChangeListener = listener;
    }

    public void setScaleInPercent(int percent) {
        double oldScale = m_scale;
        m_scale = percent / 100.0;

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        m_offsetX = centerX - (centerX - m_offsetX) * (m_scale / oldScale);
        m_offsetY = centerY - (centerY - m_offsetY) * (m_scale / oldScale);

        repaint();
    }

    private Point screenToWorld(Point screenPoint) {
        int worldX = (int) Math.round((screenPoint.x - m_offsetX) / m_scale);
        int worldY = (int) Math.round((screenPoint.y - m_offsetY) / m_scale);
        return new Point(worldX, worldY);
    }

    private void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("obstacles".equals(evt.getPropertyName())) {
            m_controller.notifyObstacleChanged();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform originalTransform = g2d.getTransform();

        g2d.translate(m_offsetX, m_offsetY);
        g2d.scale(m_scale, m_scale);

        drawTargets(g2d);
        drawObstacles(g2d);
        drawRobot(g2d,
                (int) Math.round(m_model.getRobotPositionX()),
                (int) Math.round(m_model.getRobotPositionY()),
                m_model.getRobotDirection()
        );

        g2d.setTransform(originalTransform);
    }

    private void drawTargets(Graphics2D g) {
        g.setColor(Color.GREEN);
        for (Point target : m_controller.getTargets()) {
            fillOval(g, target.x, target.y, 5, 5);
            g.setColor(Color.BLACK);
            drawOval(g, target.x, target.y, 5, 5);
            g.setColor(Color.GREEN);
        }
    }

    private void drawObstacles(Graphics2D g) {
        g.setColor(Color.RED);
        for (Point obs : m_controller.getObstacles()) {
            g.drawOval(obs.x - 20, obs.y - 20, 40, 40);
            g.fillOval(obs.x - 5, obs.y - 5, 10, 10);
        }
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        AffineTransform saveAT = g.getTransform();

        g.rotate(direction, x, y);

        g.setColor(Color.MAGENTA);
        fillOval(g, x, y, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, x + 10, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x + 10, y, 5, 5);

        g.setTransform(saveAT);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int width, int height) {
        g.fillOval(centerX - width / 2, centerY - height / 2, width, height);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int width, int height) {
        g.drawOval(centerX - width / 2, centerY - height / 2, width, height);
    }
}