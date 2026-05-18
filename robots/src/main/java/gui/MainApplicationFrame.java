package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();

    private final WindowConfig config;
    private final WindowStateManager stateManager;

    private final RobotModel robotModel;
    private final MenuManager menuManager;
    private final RobotController robotController;

    public MainApplicationFrame() {
        this.robotModel = new RobotModel();
        this.robotController = new RobotController(robotModel);

        Thread controllerThread = new Thread(robotController, "RobotControllerThread");
        controllerThread.setDaemon(true);
        controllerThread.start();

        this.config = new WindowConfig();
        this.stateManager = new WindowStateManager(config);
        this.menuManager = new MenuManager(this, robotModel);

        initializeFrame();
        setupContent();
        addDefaultWindows();
        setupMenuBar();

        restoreWindowConfiguration();
        setupWindowFocusBehavior();
        setupCloseHandler();
    }

    private void initializeFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    private void setupContent() {
        setContentPane(desktopPane);
    }

    private void addDefaultWindows() {
        LogWindow logWindow = createLogWindow();
        logWindow.setName("LogWindow");
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow(robotModel, robotController);
        gameWindow.setName("GameWindow");
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        RobotCoordinatesWindow coordWindow = new RobotCoordinatesWindow(robotModel);
        coordWindow.setName("CoordinateWindow");
        addWindow(coordWindow);

        desktopPane.moveToFront(gameWindow);
    }

    private void setupMenuBar() {
        setJMenuBar(menuManager.generateMenuBar());
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("log.status_ok");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    public void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }
    }

    private void restoreWindowConfiguration() {
        stateManager.restoreWindowState(this, desktopPane);
    }

    private void setupWindowFocusBehavior() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            addMouseListenerToAllComponents(frame, frame);
        }
    }

    private void addMouseListenerToAllComponents(JInternalFrame frame, java.awt.Component component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                bringToFront(frame);
            }
        });
        if (component instanceof java.awt.Container) {
            java.awt.Container container = (java.awt.Container) component;
            for (java.awt.Component child : container.getComponents()) {
                addMouseListenerToAllComponents(frame, child);
            }
        }
    }

    private void bringToFront(JInternalFrame frame) {
        desktopPane.moveToFront(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException ignored) {}
    }

    private void setupCloseHandler() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                performExit();
            }
        });
    }

    public void performExit() {
        Object[] options = {
                LocalizationSupport.get("dialog.exit.yes"),
                LocalizationSupport.get("dialog.exit.no")
        };

        int n = JOptionPane.showOptionDialog(this,
                LocalizationSupport.get("dialog.exit.message"),
                LocalizationSupport.get("dialog.exit.title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (n == JOptionPane.YES_OPTION) {
            saveWindowConfiguration();
            System.exit(0);
        }
    }

    private void saveWindowConfiguration() {
        config.saveWindowState("mainWindow", getBounds(), false, 0);
        JInternalFrame[] frames = desktopPane.getAllFrames();
        for (int i = 0; i < frames.length; i++) {
            JInternalFrame f = frames[i];
            String id = (f.getName() != null) ? f.getName() : f.getClass().getSimpleName();
            config.saveWindowState(id, f.getBounds(), f.isIcon(), i);
        }
    }

    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }
}