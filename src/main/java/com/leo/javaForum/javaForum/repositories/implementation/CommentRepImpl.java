package com.leo.javaForum.javaForum.repositories.implementation;


import com.leo.javaForum.javaForum.annotations.InjectLogger;
import com.leo.javaForum.javaForum.models.DTOs.CommentDTO;
import com.leo.javaForum.javaForum.models.DTOs.ReactionsDTO;
import com.leo.javaForum.javaForum.models.DTOs.types.ReactionType;
import com.leo.javaForum.javaForum.models.responseModel.ErrorResponse;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.models.responseModel.ResponseStatus;
import com.leo.javaForum.javaForum.models.responseModel.SuccessResponse;
import com.leo.javaForum.javaForum.repositories.CommentRepository;
import com.leo.javaForum.javaForum.repositories.connetion.DatabaseConnector;
import com.leo.javaForum.javaForum.repositories.connetion.PostgresqlConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CommentRepImpl implements CommentRepository {
    private Logger logger;
    private final DatabaseConnector connector;

    public CommentRepImpl(DatabaseConnector connector) {
        this.connector = connector;
    }

    //ok
    @Override
    public Response<List<CommentDTO>> getAllCommentsInPost(Long postId) {
        logger.info("Get all comments in post: " + postId);
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "SELECT * FROM public.\"Comment\" WHERE post_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, postId);
            ResultSet resultSet = statement.executeQuery();
            List<CommentDTO> comments = new ArrayList<>();

            while (resultSet.next()) {
                Long commentId = resultSet.getLong("comment_id");
                String content = resultSet.getString("content");
                String creationDate = resultSet.getString("creation_date").substring(0,10);
                Long authorId = resultSet.getLong("user_id");
                CommentDTO dto = new CommentDTO(commentId, content, creationDate, postId, authorId);
                comments.add(dto);
            }
            if (comments.isEmpty()) {
                logger.warning("No comments found in post: " + postId);
                return new ErrorResponse<>("No comments found in post", ResponseStatus.NotFound);
            }
            logger.info("Found " + comments.size() + " comments in post: " + postId);
            return new SuccessResponse<>(comments);
        } catch (SQLException e) {
            logger.severe("Failed to retrieve all comments in post: " + postId + ": " +
                    e.getMessage() + " SQL state = " + e.getSQLState());
            return new ErrorResponse<>("No comments found in post with id = " + postId,
                    ResponseStatus.InternalServerError);
        }
    }

    //ok
    @Override
    public Response<Map<String, ReactionsDTO>> getReactionsOnComment(Long commentId) {
        logger.info("Get reactions in post: " + commentId);
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "SELECT type, COUNT(*) AS count FROM public.\"Reaction\" WHERE comment_id = ? GROUP BY type";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, commentId);

            ResultSet set = statement.executeQuery();
            Map<String, ReactionsDTO> reactions = new HashMap<>();
            while (set.next()) {
                ReactionType type = (set.getString("type").equals("LIKE")) ? ReactionType.LIKE :
                        ReactionType.DISLIKE;
                int count = set.getInt("count");
                ReactionsDTO dto = new ReactionsDTO(commentId, type, count);
                reactions.put(type.name(), dto);

            }

            if (reactions.isEmpty()) {
                logger.warning("No reactions were found in comment: " + commentId);
                return new SuccessResponse<>(reactions);
            }

            logger.info("Found " + reactions.size() + " reactions in comment: " + commentId);
            return new SuccessResponse<>(reactions);
        } catch (SQLException e) {
            logger.severe("Error while getting reactions in comment: " + commentId + ": " + e.getMessage() +
                    " SQL state = " + e.getSQLState());
            return new ErrorResponse<>("Error while getting reactions", ResponseStatus.InternalServerError);
        }
    }

    @Override
    @InjectLogger
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    //ok
    @Override
    public Response<CommentDTO> create(CommentDTO entity) {
        logger.info("Create comment: " + entity.toString());
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "INSERT INTO public.\"Comment\" (content, post_id, user_id) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, entity.content());
            statement.setLong(2, entity.postId());
            statement.setLong(3, entity.userId());

            int rows = statement.executeUpdate();
            if (rows != 1) {
                logger.warning("Failed to insert new comment: " + entity.toString());
                return new ErrorResponse<>("Failed to insert new comment", ResponseStatus.InternalServerError);
            }

            logger.info("Successfully inserted new comment: " + entity.toString());
            return new SuccessResponse<>(entity);
        } catch (SQLException e) {
            logger.severe("Error while inserting new comment: " + entity.toString() + ": " + e.getMessage() +
                    " SQL state = " + e.getSQLState());
            return new ErrorResponse<>("Error while inserting new comment", ResponseStatus.InternalServerError);
        }
    }

    //ok
    @Override
    public Response<CommentDTO> delete(CommentDTO entity) {
        logger.info("Delete comment: " + entity.toString());
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "DELETE FROM public.\"Comment\" WHERE comment_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, entity.id());
            int rows = statement.executeUpdate();
            if (rows != 1) {
                logger.warning("Failed to delete comment with id: " + entity.id());
                return new ErrorResponse<>("Failed to delete comment", ResponseStatus.InternalServerError);
            }

            logger.info("Successfully deleted comment: " + entity);
            return new SuccessResponse<>(entity);
        } catch (SQLException e) {
            logger.severe("Failed to delete comment " + e.getMessage() + " SQL state = " + e.getSQLState());
            return new ErrorResponse<>("Failed to delete comment", ResponseStatus.InternalServerError);
        }
    }

    @Override
    public Response<CommentDTO> update(CommentDTO entity) {
        logger.info("Updating comment: " + entity.toString());
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "Update public.\"Comment\" SET content = ? WHERE comment_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(2, entity.id());
            statement.setString(1, entity.content());

            statement.executeUpdate();
            return new SuccessResponse<>(entity);
        } catch (SQLException e) {
            logger.severe("Failed to update comment " + e.getMessage() + " SQL state = " + e.getSQLState());
            return new ErrorResponse<>("Failed to update comment", ResponseStatus.InternalServerError);
        }
    }

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("CommentRepImpl");
        DatabaseConnector connector1 = new PostgresqlConnector();
        connector1.setLogger(logger);
        CommentRepository commentRepository = new CommentRepImpl(connector1);
        commentRepository.setLogger(logger);
        var resp = commentRepository.getReactionsOnComment(5L);
        System.out.println(resp.getData().get("LIKE").count());
    }
}
