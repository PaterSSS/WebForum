package com.leo.javaForum.javaForum.repositories.implementation;


import com.leo.javaForum.javaForum.annotations.InjectLogger;
import com.leo.javaForum.javaForum.models.DTOs.UserDTO;
import com.leo.javaForum.javaForum.models.responseModel.ErrorResponse;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.models.responseModel.ResponseStatus;
import com.leo.javaForum.javaForum.models.responseModel.SuccessResponse;
import com.leo.javaForum.javaForum.repositories.UserRepository;
import com.leo.javaForum.javaForum.repositories.connetion.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class UserRepImpl implements UserRepository {
    private Logger logger;
    private final DatabaseConnector connector;

    public UserRepImpl(DatabaseConnector connector) {
        this.connector = connector;
    }

    //ok
    @Override
    public Response<UserDTO> getById(UserDTO DTOid) {
        logger.info("getUserById");
        Connection connection;
        Long id = DTOid.id();
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }

        String query = "select public.user.username, public.user.password from public.user where user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            if (username.isEmpty()) {
                logger.info("user with DTOid " + DTOid + " not found");
                return new ErrorResponse<>("User was not found", ResponseStatus.NotFound);
            }
            UserDTO userDTO = new UserDTO(id, username, password);
            logger.info("got user with DTOid " + DTOid);
            return new SuccessResponse<>(userDTO);
        } catch (SQLException e) {
            logger.severe("Error getting user with DTOid " + DTOid + ": " + e.getMessage() + " : " + e.getSQLState());
            return new ErrorResponse<>("Error while finding user",
                    ResponseStatus.InternalServerError);
        }
    }

    //ok
    @Override
    public Response<UserDTO> findByUsername(String username) {
        logger.info("finding user by username " + username);
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "select public.user.user_id, public.user.password from public.user where username = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet set = preparedStatement.executeQuery();
            if (!set.next()) {
                logger.warning("not found user by username " + username);
                return new ErrorResponse<>("User was not found", ResponseStatus.NotFound);
            }
            String password = set.getString("password");
            Long id = set.getLong("user_id");
            UserDTO user = new UserDTO(id, username, password);
            logger.info("got user with username " + username);
            return new SuccessResponse<>(user);
        } catch (SQLException e) {
            logger.severe("Error getting user with username: " + username + " : " + e.getMessage() +
                    " : " + e.getSQLState());
            return new ErrorResponse<>("Error while finding user by username.", ResponseStatus.InternalServerError);
        }
    }
    //ok
    @Override
    public Response<UserDTO> create(UserDTO entity) {
        logger.info("create new user");
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "insert into public.user (username, password) values (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, entity.username());
            statement.setString(2, entity.password());

            int rows = statement.executeUpdate();
            if (rows != 1) {
                logger.warning("Failed to insert new user with username " + entity.username());
                return new ErrorResponse<>("Failed to insert new user", ResponseStatus.InternalServerError);
            }

            logger.info("created new user with name " + entity.username());
            return new SuccessResponse<>(new UserDTO(entity.id(), entity.username(), entity.password()));
        } catch (SQLException e) {
            logger.severe("Error creating new user: " + e.getMessage() + " : " + e.getSQLState());
            return new ErrorResponse<>("Error creating new user.", ResponseStatus.InternalServerError);
        }
    }

    @Override
    @InjectLogger
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
