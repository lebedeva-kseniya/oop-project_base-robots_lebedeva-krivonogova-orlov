package gui;

import java.awt.event.KeyEvent;
import javax.swing.*;
import log.Logger;

public class MenuManager {
    private final MainApplicationFrame frame; //ссылка на главное окно

    public MenuManager(MainApplicationFrame frame) {
        this.frame = frame;
    }

    public JMenuBar generateMenuBar() { //собирает все части меню в одну панель
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        menuBar.add(createQuitMenu());
        return menuBar;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения"); //новый объект меню
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V); //alt+M (V на англ)

        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S); //пункт меню
        systemLookAndFeel.addActionListener((event) -> { //когда пользователь нажмет, выполнить
            frame.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //меняет оформление, возвращает имя стиля ос
        });

        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            frame.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); //универсальный стиль джава
        });

        lookAndFeelMenu.add(systemLookAndFeel); //добавляет первую кнопку
        lookAndFeelMenu.add(crossplatformLookAndFeel); //вторую
        return lookAndFeelMenu;
    }

    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);

        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });

        testMenu.add(addLogMessageItem); //добавляет
        return testMenu;
    }

    private JMenu createQuitMenu() { //меню для закрытия приложения
        JMenu menu = new JMenu("Выход");
        menu.setMnemonic(KeyEvent.VK_Q);
        JMenuItem exitItem = new JMenuItem("Завершить работу", KeyEvent.VK_X);
        exitItem.addActionListener(e -> frame.performExit()); //вызывает метод
        menu.add(exitItem);
        return menu;
    }
}