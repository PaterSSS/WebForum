package com.leo.javaForum.javaForum.repositories.implementation;


import com.leo.javaForum.javaForum.annotations.InjectLogger;
import com.leo.javaForum.javaForum.models.DTOs.PostDTO;
import com.leo.javaForum.javaForum.models.DTOs.UserProfileDTO;
import com.leo.javaForum.javaForum.models.responseModel.ErrorResponse;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.models.responseModel.ResponseStatus;
import com.leo.javaForum.javaForum.models.responseModel.SuccessResponse;
import com.leo.javaForum.javaForum.repositories.UserProfileRepository;
import com.leo.javaForum.javaForum.repositories.connetion.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserProfileRepIml implements UserProfileRepository {
    private final DatabaseConnector connector;
    private Logger logger;

    public UserProfileRepIml(DatabaseConnector connector) {
        this.connector = connector;
    }

    @Override
    public Response<UserProfileDTO> getById(UserProfileDTO DTOid) {
        logger.info("getUserProfileById");
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "select public.user.username," +
                "public.user.registration_date, public.user.bio from public.user where user_id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, DTOid.id());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String username = resultSet.getString("username");
            String registrationDate = resultSet.getString("registration_date");
            String bio = resultSet.getString("bio");
            if (username.isEmpty()) {
                logger.info("user profile with DTOid " + DTOid + " not found");
                return new ErrorResponse<>("User was not found", ResponseStatus.NotFound);
            }
            UserProfileDTO userProfileDTO = new UserProfileDTO(DTOid.id(), username, registrationDate, bio);
            logger.info("got user profile with DTOid " + DTOid);
            return new SuccessResponse<>(userProfileDTO);
        } catch (SQLException e) {
            logger.severe("Error getting user profile with DTOid " + DTOid + ": " + e.getMessage() + " Sql status " + e.getSQLState());
            return new ErrorResponse<>("Error while finding user profile", ResponseStatus.InternalServerError);
        }
    }

    @Override
    public Response<UserProfileDTO> update(UserProfileDTO entity) {
        logger.info("getProfileById");
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "UPDATE public.user SET bio = ? WHERE user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, entity.bio());
            preparedStatement.setLong(2, entity.id());

            preparedStatement.executeUpdate();
            logger.info("inserted user profile with username " + entity.username());
            return new SuccessResponse<>(entity);
        } catch (SQLException e) {
            logger.severe("Failed to insert user profile with username " + entity.username() +
                    e.getMessage() + ": " + e.getSQLState());
            return new ErrorResponse<>("Failed to insert user profile", ResponseStatus.InternalServerError);
        }
    }

    @Override
    @InjectLogger
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Response<List<PostDTO>> allPostsByUserId(Long userId) {
        logger.info("get all posts that user with id:" + userId + " have");
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "select * from public.\"Post\" where user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,userId);
            ResultSet set = statement.executeQuery();
            List<PostDTO> postDTOList = new ArrayList<>();
            while (set.next()) {
                Long postId = set.getLong("post_id");
                String title = set.getString("title");
                PostDTO postDTO = new PostDTO(postId,title, null,userId, null, null);
                postDTOList.add(postDTO);
            }
            return new SuccessResponse<>(postDTOList);
        } catch (SQLException e) {
            logger.severe("Error getting all posts that user with id:" + userId + ": " + e.getMessage());
            return new ErrorResponse<>("Error while getting posts for user " + userId, ResponseStatus.InternalServerError);
        }
    }
}
