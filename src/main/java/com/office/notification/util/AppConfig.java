package com.office.notification.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private static final Properties properties = new Properties();

    static {

        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("server.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "server.properties not found"
                );
            }

            properties.load(input);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to load server.properties",
                    e
            );
        }
    }

    private AppConfig() {
    }

    public static String get(String key) {

        return properties.getProperty(key);
    }

    public static int getInt(String key) {

        return Integer.parseInt(
                properties.getProperty(key)
        );
    }
}