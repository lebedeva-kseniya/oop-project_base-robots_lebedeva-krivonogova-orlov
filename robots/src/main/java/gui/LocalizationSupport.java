package gui;

import java.text.MessageFormat;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class LocalizationSupport {
    private static final String BUNDLE_NAME = "messages";
    private static ResourceBundle resourceBundle;

    private static final Map<String, MessageFormat> mfCache = new HashMap<>();
    private static final Map<String, Formatter> fCache = new HashMap<>();
    private static final Map<String, StringBuilder> sbCache = new HashMap<>();

    static {
        try {
            setLocale(new Locale("ru"));
        } catch (Exception e) {
            resourceBundle = null;
        }
    }

    public static void setLocale(Locale locale) {
        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        } catch (Exception e) {
            resourceBundle = null;
        }
        mfCache.clear();
        fCache.clear();
        sbCache.clear();
    }

    public static String get(String key) {
        if (resourceBundle == null) return "!" + key + "!";
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }

    public static String formatWithFormatter(String pattern, Object... args) {
        return String.format(pattern, args);
    }

    public static String formatWithFormatterCache(String pattern, Object... args) {
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

    public static String formatWithMessageFormat(String pattern, Object... args) {
        return MessageFormat.format(pattern, args);
    }

    public static String formatWithMessageFormatCache(String pattern, Object... args) {
        MessageFormat mf = mfCache.get(pattern);
        if (mf == null) {
            mf = new MessageFormat(pattern);
            mfCache.put(pattern, mf);
        }
        return mf.format(args);
    }

    public static String format(String key, Object... args) {
        return formatWithMessageFormatCache(get(key), args);
    }
}