package com.leo.javaForum.javaForum.controllers.configuration;

import com.leo.javaForum.javaForum.context.AppContextProvider;
import com.leo.javaForum.javaForum.context.Context;
import com.leo.javaForum.javaForum.repositories.connetion.DatabaseConnector;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    private final Context context = AppContextProvider.getFilledContext();

    @Bean
    public Context getContext() {
        return context;
    }

    @PreDestroy
    public void destroy() {
        DatabaseConnector connector = context.getBean(DatabaseConnector.class);
        connector.closeConnection();
    }
}
