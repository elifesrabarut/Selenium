package com.hepsiburada.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("config.properties yüklenemedi: " + e.getMessage());
        }
    }

    public static String get(String key, String defaultValue) {
        String systemProperty = System.getProperty(key);
        if (systemProperty != null) {
            return systemProperty;
        }
        return properties.getProperty(key, defaultValue);
    }

    public static String get(String key) {
        return get(key, null);
    }

    public static String getBaseUrl() {
        return get("base.url", "https://www.hepsiburada.com");
    }

    public static String getBrowser() {
        return get("browser", "chrome");
    }

    public static int getImplicitWait() {
        return Integer.parseInt(get("implicit.wait", "10"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(get("explicit.wait", "15"));
    }
}
