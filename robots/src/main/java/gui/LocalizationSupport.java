package gui;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class LocalizationSupport {
    private static final String BUNDLE_NAME = "messages";
    private static ResourceBundle resourceBundle;
    private static final Map<String, MessageFormat> mfCache = new HashMap<>();

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
    }

    public static String get(String key) {
        if (resourceBundle == null) return "!" + key + "!";
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }

    public static String formatWithMessageFormatCache(String pattern, Object... args) {
        MessageFormat mf = mfCache.get(pattern);
        if (mf == null) {
            mf = new MessageFormat(pattern, Locale.US);
            mfCache.put(pattern, mf);
        }
        return mf.format(args);
    }

    public static String format(String key, Object... args) {
        return formatWithMessageFormatCache(get(key), args);
    }
}