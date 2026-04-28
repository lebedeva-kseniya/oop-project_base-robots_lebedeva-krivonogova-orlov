package gui;

import log.Logger;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
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
        this.m_model.addPropertyChangeListener(this);

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
                    m_controller.addTarget(e.getPoint());
                    Logger.debug("Точка добавлена в очередь: " + e.getPoint());
                } else if (SwingUtilities.isRightMouseButton(e)) {

                    boolean removed = m_model.removeObstacleAt(clickPoint);

                    if (removed) {
                        m_controller.notifyObstacleChanged();
                        Logger.debug("Препятствие удалено: " + clickPoint);
                    } else {
                        m_model.addObstacle(clickPoint);
                        m_controller.notifyObstacleChanged();
                        Logger.debug("Препятствие установлено: " + clickPoint);
                    }
                }
            }
        });

        setDoubleBuffered(true);
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        repaint();
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawTargets(g2d);

        drawObstacles(g2d);

        drawRobot(g2d,
                round(m_model.getRobotPositionX()),
                round(m_model.getRobotPositionY()),
                m_model.getRobotDirection());
    }

    private void drawTargets(Graphics2D g) {
        AffineTransform saveAT = g.getTransform();
        g.setTransform(new AffineTransform());

        g.setColor(Color.GREEN);
        for (Point target : m_controller.getTargets()) {
            fillOval(g, target.x, target.y, 5, 5);
            g.setColor(Color.BLACK);
            drawOval(g, target.x, target.y, 5, 5);
            g.setColor(Color.GREEN);
        }

        g.setTransform(saveAT);
    }

    private void drawObstacles(Graphics2D g) {
        g.setColor(Color.RED);
        for (Point obs : m_model.getObstacles()) {
            g.drawOval(obs.x - 20, obs.y - 20, 40, 40);
            g.fillOval(obs.x - 5, obs.y - 5, 10, 10);
        }
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        AffineTransform saveAT = g.getTransform();

        AffineTransform t = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(t);

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

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
}