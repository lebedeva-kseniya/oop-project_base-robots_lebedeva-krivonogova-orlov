package log;

public class LogEntry {
    private final LogLevel level;
    private final String key;
    private final Object[] args;

    public LogEntry(LogLevel level, String key, Object... args) {
        this.level = level;
        this.key = key;
        this.args = args;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getKey() {
        return key;
    }

    public Object[] getArgs() {
        return args;
    }
}