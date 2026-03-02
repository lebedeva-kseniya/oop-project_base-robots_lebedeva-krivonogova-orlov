package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import log.Logger;

public class MainApplicationFrame extends JFrame { //наследует функционал стандартного окна ос
    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane); //делает рабочий стол главное областью окна

        addWindow(createLogWindow()); //начальные окна
        addWindow(new GameWindow()); //размер задается внутри или через pack()

        MenuManager menuManager = new MenuManager(this); //инициализация через отдельный класс
        setJMenuBar(menuManager.generateMenuBar());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //обработка выхода из приложения
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                performExit(); //после нажатия
            }
        });
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) { //логика добавления
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    public void setLookAndFeel(String className) { //паблик чтобы MM мог вызвать
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            // ignore
        }
    }

    public void performExit() { //диалог подтверждения на русском
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