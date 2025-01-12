package com.leo.javaForum.javaForum.repositories.connetion;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties;

    static {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream("C:\\side-projects\\javaForum\\src\\main\\resources\\application.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file.");
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
