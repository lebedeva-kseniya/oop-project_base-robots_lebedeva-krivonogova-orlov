package gui;

public class PerformanceAnalyzer {

    public static String runAnalysis() {
        String patternMF = "Робот в позиции {0}, {1}. Статус: {2}";
        String patternF = "Робот в позиции %s, %s. Статус: %s";
        Object[] args = {10.5, 20.3, "Active"};
        int iterations = 100000;

        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            LocalizationSupport.formatWithFormatter(patternF, args);
        }
        long timeF = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            LocalizationSupport.formatWithFormatterCache(patternF, args);
        }
        long timeFCache = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            LocalizationSupport.formatWithMessageFormat(patternMF, args);
        }
        long timeMF = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            LocalizationSupport.formatWithMessageFormatCache(patternMF, args);
        }
        long timeMFCache = System.nanoTime() - start;

        StringBuilder sb = new StringBuilder();
        sb.append("Результаты анализа (").append(iterations).append(" итераций):\n\n");
        sb.append("1. Formatter без кэша: ").append(timeF / 1_000_000).append(" ms\n");
        sb.append("2. Formatter с кэшем: ").append(timeFCache / 1_000_000).append(" ms\n");
        sb.append("3. MessageFormat без кэша: ").append(timeMF / 1_000_000).append(" ms\n");
        sb.append("4. MessageFormat с кэшем: ").append(timeMFCache / 1_000_000).append(" ms");

        return sb.toString();
    }
}