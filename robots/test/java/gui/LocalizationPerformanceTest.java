package gui;

import log.LogWindowSource;
import log.LogLevel;
import org.junit.jupiter.api.Test;
import java.text.MessageFormat;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class LocalizationPerformanceTest {

    private final Map<String, Formatter> fCache = new HashMap<>();
    private final Map<String, StringBuilder> sbCache = new HashMap<>();

    private String formatWithFormatterCache(String pattern, Object... args) {
        Formatter f = fCache.get(pattern);
        StringBuilder sb = sbCache.get(pattern);
        if (f == null) {
            sb = new StringBuilder();
            f = new Formatter(sb, Locale.US);
            fCache.put(pattern, f);
            sbCache.put(pattern, sb);
        }
        sb.setLength(0);
        f.format(Locale.US, pattern, args);
        return sb.toString();
    }

    @Test
    public void testLogWindowSourceQueue() {
        LogWindowSource source = new LogWindowSource(2);
        source.append(LogLevel.Debug, "test1");
        source.append(LogLevel.Debug, "test2");
        source.append(LogLevel.Debug, "test3");
        assertEquals(2, source.size());
    }

    @Test
    public void testPerformanceComparison() {
        String patternMF = "Робот в позиции {0}, {1}. Статус: {2}";
        String patternF = "Робот в позиции %s, %s. Статус: %s";
        Object[] args = {10.5, 20.3, "Active"};
        String expected = "Робот в позиции 10.5, 20.3. Статус: Active";
        int iterations = 10000;

        String formattedF = String.format(Locale.US, patternF, args);
        long startF = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            String.format(Locale.US, patternF, args);
        }
        long timeF = System.nanoTime() - startF;
        assertEquals(expected, formattedF);

        String formattedFCache = formatWithFormatterCache(patternF, args);
        long startFCache = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            formatWithFormatterCache(patternF, args);
        }
        long timeFCache = System.nanoTime() - startFCache;
        assertEquals(expected, formattedFCache);

        String formattedMF = new MessageFormat(patternMF, Locale.US).format(args);
        long startMF = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            new MessageFormat(patternMF, Locale.US).format(args);
        }
        long timeMF = System.nanoTime() - startMF;
        assertEquals(expected, formattedMF);

        String formattedMFCache = LocalizationSupport.formatWithMessageFormatCache(patternMF, args);
        long startMFCache = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            LocalizationSupport.formatWithMessageFormatCache(patternMF, args);
        }
        long timeMFCache = System.nanoTime() - startMFCache;
        assertEquals(expected, formattedMFCache);

        assertTrue(timeMFCache < timeMF);
    }
}