package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowConfig config;
    private final WindowStateManager stateManager;
    private final MenuBarBuilder menuBarBuilder;

    public MainApplicationFrame() {
        this.config = new WindowConfig();
        this.stateManager = new WindowStateManager(config);
        this.menuBarBuilder = new MenuBarBuilder(this);

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

        GameWindow gameWindow = createGameWindow();
        gameWindow.setName("GameWindow");
        addWindow(gameWindow);

        desktopPane.moveToFront(gameWindow);
        desktopPane.moveToBack(logWindow);
    }

    private void setupMenuBar() {
        setJMenuBar(menuBarBuilder.createMenuBar());
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected GameWindow createGameWindow() {
        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        return gameWindow;
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
        System.out.println("Настройка поведения фокуса для окон...");

        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            addMouseListenerToAllComponents(frame, frame);
        }

        System.out.println("Поведение фокуса настроено для " +
                desktopPane.getAllFrames().length + " окон");
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
        } catch (java.beans.PropertyVetoException e) {
        }
        System.out.println("Окно " + frame.getName() + " поднято на передний план");
    }

    private void setupCloseHandler() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.out.println("Сохранение конфигурации перед закрытием...");
                saveWindowConfiguration();
                dispose();
                System.exit(0);
            }
        });
    }

    private void saveWindowConfiguration() {
        java.awt.Rectangle frameBounds = getBounds();
        config.saveWindowState("mainWindow", frameBounds, false, 0);

        JInternalFrame[] frames = desktopPane.getAllFrames();
        for (int i = 0; i < frames.length; i++) {
            JInternalFrame internalFrame = frames[i];
            if (internalFrame.isVisible()) {
                String windowId = internalFrame.getName();
                if (windowId == null || windowId.isEmpty()) {
                    windowId = internalFrame.getClass().getSimpleName();
                }

                java.awt.Rectangle bounds = internalFrame.getBounds();
                boolean minimized = internalFrame.isIcon();
                int zOrder = i;

                config.saveWindowState(windowId, bounds, minimized, zOrder);
                System.out.println("Сохранено окно: " + windowId);
            }
        }

        System.out.println("Конфигурация окон сохранена");
    }
}