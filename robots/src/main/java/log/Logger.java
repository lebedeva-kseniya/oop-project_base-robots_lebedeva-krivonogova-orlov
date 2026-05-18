package log;

public final class Logger
{
    private static final LogWindowSource defaultLogSource;
    static {
        defaultLogSource = new LogWindowSource(100);
    }

    private Logger()
    {
    }

    public static void debug(String key, Object... args)
    {
        defaultLogSource.append(LogLevel.Debug, key, args);
    }

    public static void debug(LogEntry entry)
    {
        defaultLogSource.append(entry.getLevel(), entry.getKey(), entry.getArgs());
    }

    public static void error(String key, Object... args)
    {
        defaultLogSource.append(LogLevel.Error, key, args);
    }

    public static LogWindowSource getDefaultLogSource()
    {
        return defaultLogSource;
    }
}