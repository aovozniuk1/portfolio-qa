package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads configuration from {@code config.properties} with system-property overrides.
 * <p>
 * System properties (e.g. {@code -Dbase.url=...}) take precedence over the file values.
 */
public final class ConfigReader {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream is = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (is != null) {
                PROPS.load(is);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Cannot load config.properties: " + e.getMessage());
        }
    }

    private ConfigReader() {
        // utility class
    }

    /**
     * Get a property value. System properties override file values.
     *
     * @param key property key
     * @return property value, or {@code null} if not found
     */
    public static String get(String key) {
        String sys = System.getProperty(key);
        return sys != null ? sys : PROPS.getProperty(key);
    }

    /**
     * Get a property with a fallback default.
     *
     * @param key          property key
     * @param defaultValue fallback
     * @return resolved value
     */
    public static String get(String key, String defaultValue) {
        String val = get(key);
        return val != null ? val : defaultValue;
    }

    public static String getBaseUrl() {
        return get("base.url", "https://the-internet.herokuapp.com");
    }

    public static String getApiBaseUrl() {
        return get("api.base.url", "https://jsonplaceholder.typicode.com");
    }

    public static String getBrowser() {
        return get("browser", "chrome");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(get("headless", "true"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(get("explicit.wait", "10"));
    }

    public static int getPageLoadTimeout() {
        return Integer.parseInt(get("page.load.timeout", "30"));
    }

    public static int getRetryCount() {
        return Integer.parseInt(get("retry.count", "2"));
    }
}
