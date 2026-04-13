package gui;

import java.awt.event.KeyEvent;
import javax.swing.*;
import log.Logger;

public class MenuManager {
    private final MainApplicationFrame frame; // Ссылка на главное окно
    private final RobotModel robotModel;       // Ссылка на модель робота

    //конструктор теперь принимает и фрейм, и модель
    public MenuManager(MainApplicationFrame frame, RobotModel robotModel) {
        this.frame = frame;
        this.robotModel = robotModel;
    }

    public JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        menuBar.add(createQuitMenu());
        return menuBar;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);

        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            frame.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        });

        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_U); // Изменил на U, чтобы не дублировать S
        crossplatformLookAndFeel.addActionListener((event) -> {
            frame.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        });

        lookAndFeelMenu.add(systemLookAndFeel);
        lookAndFeelMenu.add(crossplatformLookAndFeel);
        return lookAndFeelMenu;
    }

    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);

        // Стандартный тест лога
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });

        JMenuItem showRobotCoordsItem = new JMenuItem("Показать координаты робота", KeyEvent.VK_R);
        showRobotCoordsItem.addActionListener((event) -> {
            Logger.debug("X: " + robotModel.getRobotPositionX() +
                    ", Y: " + robotModel.getRobotPositionY() +
                    ", Направление: " + Math.toDegrees(robotModel.getRobotDirection()) + "°");
        });

        testMenu.add(addLogMessageItem);
        testMenu.add(showRobotCoordsItem); // Добавляем новый пункт
        return testMenu;
    }

    private JMenu createQuitMenu() {
        JMenu menu = new JMenu("Выход");
        menu.setMnemonic(KeyEvent.VK_Q);
        JMenuItem exitItem = new JMenuItem("Завершить работу", KeyEvent.VK_X);
        exitItem.addActionListener(e -> frame.performExit());
        menu.add(exitItem);
        return menu;
    }
}