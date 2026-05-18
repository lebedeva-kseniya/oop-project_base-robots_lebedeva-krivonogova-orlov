package gui;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame {
    private final GameVisualizer m_visualizer;

    public GameWindow(RobotModel model, RobotController controller) {
        super(LocalizationSupport.get("window.title.game"), true, true, true, true);
        m_visualizer = new GameVisualizer(model, controller);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
}