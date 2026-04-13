package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import log.Logger;

/**
 * Главное окно приложения, наследующее функционал стандартного окна ОС (JFrame).
 */
public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    // Из первого файла: модель робота для синхронизации данных между окнами
    private final RobotModel robotModel;

    public MainApplicationFrame() {
        // Инициализируем модель робота
        robotModel = new RobotModel();

        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        // Делаем рабочий стол главной областью окна
        setContentPane(desktopPane);

        // Инициализация и добавление окон
        addWindow(createLogWindow());

        // Передаем robotModel в GameWindow, как в первом файле
        GameWindow gameWindow = new GameWindow(robotModel);
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        // Добавляем окно координат робота из первого файла
        RobotCoordinatesWindow coordWindow = new RobotCoordinatesWindow(robotModel);
        addWindow(coordWindow);

        // Инициализация меню через отдельный класс (структура второго файла)
        MenuManager menuManager = new MenuManager(this, robotModel);
        setJMenuBar(menuManager.generateMenuBar());

        // Настройка закрытия приложения с подтверждением
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                performExit();
            }
        });
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

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Изменение внешнего вида (LookAndFeel).
     * Сделан public, чтобы MenuManager мог вызывать его.
     */
    public void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            // Игнорируем ошибки при смене темы
        }
    }

    /**
     * Диалог подтверждения выхода на русском языке.
     */
    public void performExit() {
        Object[] options = {"Да", "Нет"};
        int n = JOptionPane.showOptionDialog(this,
                "Вы действительно хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (n == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}