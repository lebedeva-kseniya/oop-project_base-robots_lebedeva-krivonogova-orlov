package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

public class LogWindow extends JInternalFrame implements LogChangeListener {
    private LogWindowSource m_logSource;
    private TextArea m_logContent;

    public LogWindow(LogWindowSource logSource) {
        super(LocalizationSupport.get("window.title.log"), true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all()) {
            String rawMessage = entry.getMessage();
            String translated;

            if (rawMessage.contains("|")) {
                String[] parts = rawMessage.split("\\|");
                String key = parts[0];

                String pattern = LocalizationSupport.get(key);

                if (!pattern.startsWith("!")) {
                    Object[] args = new Object[parts.length - 1];
                    System.arraycopy(parts, 1, args, 0, parts.length - 1);
                    translated = java.text.MessageFormat.format(pattern, args);
                } else {
                    translated = rawMessage;
                }
            } else {
                translated = LocalizationSupport.get(rawMessage);
                if (translated.startsWith("!")) {
                    translated = rawMessage;
                }
            }
            content.append(translated).append("\n");
        }
        m_logContent.setText(content.toString());
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }
}