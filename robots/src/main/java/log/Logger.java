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

    {
    }
    
    {
    }

    public static LogWindowSource getDefaultLogSource()
    {
        return defaultLogSource;
    }
}