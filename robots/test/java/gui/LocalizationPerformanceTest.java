package gui;

import org.junit.jupiter.api.Test;
import java.text.MessageFormat;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

public class LocalizationPerformanceTest {

    private final Map<String, Formatter> fCache = new HashMap<>();
    private final Map<String, StringBuilder> sbCache = new HashMap<>();

    private String formatWithFormatterCacheLocal(String pattern, Object... args) {
        Formatter f = fCache.get(pattern);
        StringBuilder sb = sbCache.get(pattern);
        if (f == null) {
            sb = new StringBuilder();
            f = new Formatter(sb);
            fCache.put(pattern, f);
            sbCache.put(pattern, sb);
        }
        sb.setLength(0);
        f.format(pattern, args);
        return sb.toString();
    }

    @Test
    public void testLocalizationPerformance() {
        String patternMF = "Робот в позиции {0}, {1}. Статус: {2}";
        String patternF = "Робот в позиции %s, %s. Статус: %s";
        Object[] messageArgs = {10.5, 20.3, "Active"};
        int iterations = 100000;

        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            String.format(patternF, messageArgs);
        }
        long timeF = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            formatWithFormatterCacheLocal(patternF, messageArgs);
        }
        long timeFCache = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            MessageFormat.format(patternMF, messageArgs);
        }
        long timeMF = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            LocalizationSupport.formatWithMessageFormatCache(patternMF, messageArgs);
        }
        long timeMFCache = System.nanoTime() - start;

        System.out.println("Результаты анализа производительности (" + iterations + " итераций):");
        System.out.println("1. Formatter без кэша: " + (timeF / 1_000_000) + " ms");
        System.out.println("2. Formatter с кэшем: " + (timeFCache / 1_000_000) + " ms");
        System.out.println("3. MessageFormat без кэша: " + (timeMF / 1_000_000) + " ms");
        System.out.println("4. MessageFormat с кэшем (Используется): " + (timeMFCache / 1_000_000) + " ms");
    }
}