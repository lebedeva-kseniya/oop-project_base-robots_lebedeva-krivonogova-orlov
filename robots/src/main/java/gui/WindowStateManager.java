package gui;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class WindowStateManager {
    private static final String MAIN_WINDOW_ID = "mainWindow";
    private final WindowConfig config;

    public WindowStateManager(WindowConfig config) {
        this.config = config;
    }

    public void restoreWindowState(MainApplicationFrame frame, JDesktopPane desktopPane) {
        System.out.println("Восстановление состояния окон...");

        restoreMainWindowState(frame);
        restoreInternalWindowsState(desktopPane);
    }

    private void restoreMainWindowState(MainApplicationFrame frame) {
        WindowState mainState = config.getWindowState(MAIN_WINDOW_ID);
        if (mainState != null) {
            Rectangle bounds = mainState.getBounds();
            java.awt.Rectangle awtBounds = new java.awt.Rectangle(
                    bounds.getX(), bounds.getY(),
                    bounds.getWidth(), bounds.getHeight()
            );
            frame.setBounds(awtBounds);
            System.out.println("Восстановлено главное окно: " + awtBounds);
        } else {
            System.out.println("Главное окно не найдено в конфигурации");
        }
    }

    private void restoreInternalWindowsState(JDesktopPane desktopPane) {
        JInternalFrame[] frames = desktopPane.getAllFrames();

        for (JInternalFrame internalFrame : frames) {
            String windowId = getWindowId(internalFrame);
            WindowState windowState = config.getWindowState(windowId);

            if (windowState != null) {
                Rectangle bounds = windowState.getBounds();
                java.awt.Rectangle awtBounds = new java.awt.Rectangle(
                        bounds.getX(), bounds.getY(),
                        bounds.getWidth(), bounds.getHeight()
                );

                internalFrame.setBounds(awtBounds);
                restoreMinimizedState(internalFrame, windowState);

                System.out.println("Восстановлено окно " + windowId +
                        ": bounds=[" + awtBounds.x + "," + awtBounds.y +
                        "," + awtBounds.width + "," + awtBounds.height + "]");
            } else {
                System.out.println("Окно " + windowId + " не найдено в конфигурации");
            }
        }

        restoreZOrder(desktopPane);
    }

    private void restoreZOrder(JDesktopPane desktopPane) {
        JInternalFrame[] frames = desktopPane.getAllFrames();

        java.util.ArrayList<JInternalFrame> sortedFrames = new java.util.ArrayList<>();
        for (JInternalFrame frame : frames) {
            sortedFrames.add(frame);
        }

        sortedFrames.sort((f1, f2) -> {
            String id1 = getWindowId(f1);
            String id2 = getWindowId(f2);
            WindowState state1 = config.getWindowState(id1);
            WindowState state2 = config.getWindowState(id2);

            int order1 = (state1 != null) ? state1.getLayer() : 0;
            int order2 = (state2 != null) ? state2.getLayer() : 0;

            return Integer.compare(order1, order2);
        });

        for (int i = sortedFrames.size() - 1; i >= 0; i--) {
            desktopPane.moveToFront(sortedFrames.get(i));
        }

        desktopPane.repaint();
    }

    private void restoreMinimizedState(JInternalFrame frame, WindowState state) {
        if (state.isMinimized()) {
            try {
                frame.setIcon(true);
            } catch (java.beans.PropertyVetoException e) {
                System.err.println("Ошибка восстановления minimized состояния: " + e.getMessage());
            }
        }
    }

    private String getWindowId(JInternalFrame frame) {
        String windowId = frame.getName();
        if (windowId == null || windowId.isEmpty()) {
            windowId = frame.getClass().getSimpleName();
        }
        return windowId;
    }
}