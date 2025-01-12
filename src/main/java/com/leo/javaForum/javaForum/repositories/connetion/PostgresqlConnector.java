package com.leo.javaForum.javaForum.repositories.connetion;


import com.leo.javaForum.javaForum.annotations.InjectLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
//есть вариант реализвации с пуллом подключений, но я пока оставлю одно, которое будет закрываться при завершении приложения.
//в данной реализации будет постоянно работать одно соединение, которо будет автоматически восстанавливаться, если предыдущее закрылось
//по каким-либо причинам.
public class PostgresqlConnector implements DatabaseConnector {
    private Connection connection;

    private Logger logger;

    @Override
    public Connection getConnection() throws RuntimeException {
        logger.info("Connecting to PostgreSQL database...");
            try {
                if (isConnectionClosed()) {
                    Class.forName(ConfigLoader.getProperty("db.driver"));
                    logger.info("Loaded driver");
                    String url = ConfigLoader.getProperty("db.url");
                    logger.info("Loaded url " + url);
                    String password = ConfigLoader.getProperty("db.password");
                    logger.info("Loaded password " + password);
                    String username = ConfigLoader.getProperty("db.username");
                    logger.info("Loaded username " + username);
                    connection = DriverManager.getConnection(url, username, password);
                    logger.info("Successfully connected to PostgreSQL database.");
                }
            } catch (SQLException | ClassNotFoundException e) {

                logger.severe("Failed to connect to PostgreSQL database.");

                throw new RuntimeException("Failed to connect to database", e);
            }
        return connection;
    }

    @Override
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Successfully closed PostgreSQL database.");
            } catch (SQLException e) {
                logger.severe("Failed to close PostgreSQL database." + e.getMessage());
            }
        }
    }

    private boolean isConnectionClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            logger.severe("Failed to close PostgreSQL database." + e.getMessage());
            return true; // Closed by fail
        }
    }

    @Override
    @InjectLogger
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
