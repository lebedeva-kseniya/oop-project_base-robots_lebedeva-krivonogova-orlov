package main.java.gui;

import gui.LocalizationSupport;
import java.text.MessageFormat;

public class LocalizationPerformanceTest {

    public static void main(String[] args) {
        String patternMF = "Робот в позиции {0}, {1}. Статус: {2}";
        String patternF = "Робот в позиции %s, %s. Статус: %s";
        Object[] messageArgs = {10.5, 20.3, "Active"};
        int iterations = 100000;

        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            LocalizationSupport.formatWithFormatter(patternF, messageArgs);
        }
        long timeF = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            LocalizationSupport.formatWithFormatterCache(patternF, messageArgs);
        }
        long timeFCache = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            LocalizationSupport.formatWithMessageFormat(patternMF, messageArgs);
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
        System.out.println("4. MessageFormat с кэшем: " + (timeMFCache / 1_000_000) + " ms");
    }
}