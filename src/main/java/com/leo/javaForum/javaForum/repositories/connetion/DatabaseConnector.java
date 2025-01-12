package com.leo.javaForum.javaForum.repositories.connetion;



import com.leo.javaForum.javaForum.logger.Loggable;

import java.sql.Connection;

public interface DatabaseConnector extends Loggable {
    int MAX_RETRIES = 3;

    Connection getConnection() throws RuntimeException;

    void closeConnection();
}
