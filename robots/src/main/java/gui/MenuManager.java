package gui;

import java.awt.event.KeyEvent;
import java.util.Locale;
import javax.swing.*;
import log.Logger;

public class MenuManager {
    private final MainApplicationFrame frame;
    private final RobotModel robotModel;

    public MenuManager(MainApplicationFrame frame, RobotModel robotModel) {
        this.frame = frame;
        this.robotModel = robotModel;
    }

    public JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createLanguageMenu());
        menuBar.add(createTestMenu());
        menuBar.add(createQuitMenu());
        return menuBar;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu(LocalizationSupport.get("menu.view_mode"));
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);

        JMenuItem systemLookAndFeel = new JMenuItem(LocalizationSupport.get("menu.view_mode.system"), KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            frame.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        });

        JMenuItem crossplatformLookAndFeel = new JMenuItem(LocalizationSupport.get("menu.view_mode.universal"), KeyEvent.VK_U);
        crossplatformLookAndFeel.addActionListener((event) -> {
            frame.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        });

        lookAndFeelMenu.add(systemLookAndFeel);
        lookAndFeelMenu.add(crossplatformLookAndFeel);
        return lookAndFeelMenu;
    }

    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu(LocalizationSupport.get("menu.tests"));
        testMenu.setMnemonic(KeyEvent.VK_T);

        JMenuItem addLogMessageItem = new JMenuItem(LocalizationSupport.get("menu.tests.log_message"), KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("log.message.new_line");
        });

        JMenuItem showRobotCoordsItem = new JMenuItem(LocalizationSupport.get("menu.tests.robot_coords"), KeyEvent.VK_R);
        showRobotCoordsItem.addActionListener((event) -> {
            Logger.debug(robotModel.getCoordsLogEntry());
        });

        testMenu.add(addLogMessageItem);
        testMenu.add(showRobotCoordsItem);
        return testMenu;
    }

    private JMenu createQuitMenu() {
        JMenu menu = new JMenu(LocalizationSupport.get("menu.exit"));
        menu.setMnemonic(KeyEvent.VK_Q);

        JMenuItem exitItem = new JMenuItem(LocalizationSupport.get("menu.exit.quit"), KeyEvent.VK_X);
        exitItem.addActionListener(e -> frame.performExit());
        menu.add(exitItem);
        return menu;
    }

    private JMenu createLanguageMenu() {
        JMenu langMenu = new JMenu(LocalizationSupport.get("menu.language"));

        JMenuItem russian = new JMenuItem("Русский");
        russian.addActionListener(e -> {
            LocalizationSupport.setLocale(new Locale("ru"));
            updateInterface();
        });

        JMenuItem english = new JMenuItem("English");
        english.addActionListener(e -> {
            LocalizationSupport.setLocale(new Locale("en"));
            updateInterface();
        });

        langMenu.add(russian);
        langMenu.add(english);
        return langMenu;
    }

    private void updateInterface() {
        frame.setJMenuBar(generateMenuBar());

        for (JInternalFrame internalFrame : frame.getDesktopPane().getAllFrames()) {
            if (internalFrame instanceof RobotCoordinatesWindow) {
                ((RobotCoordinatesWindow) internalFrame).updateNames();
            } else if (internalFrame instanceof GameWindow) {
                internalFrame.setTitle(LocalizationSupport.get("window.title.game"));
            } else if (internalFrame instanceof LogWindow) {
                internalFrame.setTitle(LocalizationSupport.get("window.title.log"));
                ((LogWindow) internalFrame).onLogChanged();
            }
        }

        SwingUtilities.updateComponentTreeUI(frame);
    }
}