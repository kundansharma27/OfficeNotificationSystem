package com.office.notification.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private static final Properties properties = new Properties();

    static {

        // 1. First try external server.properties
        File externalFile =
                new File("server.properties");

        if (externalFile.exists()) {

            try (InputStream input =
                         new FileInputStream(externalFile)) {

                properties.load(input);

            } catch (IOException e) {

                throw new RuntimeException(
                        "Failed to load external server.properties",
                        e
                );
            }

        } else {

            // 2. Fallback to packaged server.properties
            try (InputStream input =
                         AppConfig.class
                                 .getClassLoader()
                                 .getResourceAsStream(
                                         "server.properties"
                                 )) {

                if (input == null) {

                    throw new IllegalStateException(
                            "server.properties not found"
                    );
                }

                properties.load(input);

            } catch (IOException e) {

                throw new RuntimeException(
                        "Failed to load packaged server.properties",
                        e
                );
            }
        }
    }

    private AppConfig() {
    }

    public static String get(String key) {

        String value =
                properties.getProperty(key);

        if (value == null) {

            throw new IllegalArgumentException(
                    "Missing configuration property: "
                            + key
            );
        }

        return value.trim();
    }

    public static int getInt(String key) {

        try {

            return Integer.parseInt(
                    get(key)
            );

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Invalid integer value for property: "
                            + key,
                    e
            );
        }
    }
}