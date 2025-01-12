package com.leo.javaForum.javaForum.logger;

import java.io.IOException;
import java.util.logging.*;

public class AppLogger {
    private static final Logger logger = Logger.getLogger("GlobalAppLogger");

    static {
        try {
            // Отключаем стандартные обработчики, чтобы избежать дублирования вывода
            LogManager.getLogManager().reset();

            // Создаём обработчик для записи логов в файл
            FileHandler fileHandler = new FileHandler("C:\\side-projects\\javaForum\\logs\\application.log");
            fileHandler.setLevel(Level.ALL);
            fileHandler.setEncoding("UTF-8");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            // Устанавливаем глобальный уровень логирования
            logger.setLevel(Level.ALL);

            logger.info("Единый логгер инициализирован.");
        } catch (IOException e) {
            System.err.println("Ошибка при настройке логгера: " + e.getMessage());
        }
    }

    private AppLogger() {}

    public static Logger getLogger() {
        return logger;
    }
}
