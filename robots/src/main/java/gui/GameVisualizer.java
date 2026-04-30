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
            public void mouseClicked(MouseEvent e) {
                Point clickPoint = e.getPoint();

                if (SwingUtilities.isLeftMouseButton(e)) {
                    m_controller.addTarget(clickPoint);
                    Logger.debug("Цель установлена: " + clickPoint.x + ", " + clickPoint.y);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    if (m_controller.removeObstacleAt(clickPoint)) {
                        Logger.debug("Препятствие удалено в точке: " + clickPoint.x + ", " + clickPoint.y);
                    } else {
                        m_controller.addObstacle(clickPoint);
                        Logger.debug("Препятствие установлено: " + clickPoint.x + ", " + clickPoint.y);
                    }
                }
            }
        });

        setDoubleBuffered(true);
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
        drawTargets(g2d);
        drawObstacles(g2d);
        drawRobot(g2d,
                (int) Math.round(m_model.getRobotPositionX()),
                (int) Math.round(m_model.getRobotPositionY()),
                m_model.getRobotDirection()
        );
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