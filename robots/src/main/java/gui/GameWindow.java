package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.BorderFactory;

public class GameWindow extends JInternalFrame {
    private final GameVisualizer m_visualizer;

    public GameWindow(RobotModel model, RobotController controller) {
        super(LocalizationSupport.get("window.title.game"), true, true, true, true);

        m_visualizer = new GameVisualizer(model, controller);

        JSlider zoomSlider = new JSlider(
                JSlider.VERTICAL,
                GameVisualizer.MIN_SCALE_PCT,
                GameVisualizer.MAX_SCALE_PCT,
                GameVisualizer.DEFAULT_SCALE_PCT
        );

        zoomSlider.setMajorTickSpacing(100);
        zoomSlider.setMinorTickSpacing(25);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setPaintLabels(false);
        zoomSlider.setOpaque(false);

        zoomSlider.setPreferredSize(new Dimension(30, 200));

        zoomSlider.addChangeListener(e -> {
            if (zoomSlider.getValueIsAdjusting()) {
                m_visualizer.setScaleInPercent(zoomSlider.getValue());
            }
        });

        m_visualizer.setScaleChangeListener(currentPercent -> {
            zoomSlider.setValue(currentPercent);
        });

        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        sliderPanel.setOpaque(false);
        sliderPanel.add(zoomSlider, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(m_visualizer, BorderLayout.CENTER);
        mainPanel.add(sliderPanel, BorderLayout.EAST);

        getContentPane().add(mainPanel);
        pack();
    }
}