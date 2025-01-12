package com.leo.javaForum.javaForum.repositories.implementation;

import com.leo.javaForum.javaForum.annotations.InjectLogger;
import com.leo.javaForum.javaForum.models.DTOs.ReactionDTO;
import com.leo.javaForum.javaForum.models.DTOs.types.ReactionType;
import com.leo.javaForum.javaForum.models.responseModel.ErrorResponse;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.models.responseModel.ResponseStatus;
import com.leo.javaForum.javaForum.models.responseModel.SuccessResponse;
import com.leo.javaForum.javaForum.repositories.ReactionRepository;
import com.leo.javaForum.javaForum.repositories.connetion.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

//вставка типа коммента будет больш
public class ReactionRepImpl implements ReactionRepository {
    private Logger logger;
    private final DatabaseConnector connector;

    public ReactionRepImpl(DatabaseConnector connector) {
        this.connector = connector;
    }

    //ok
    @Override
    public Response<Boolean> isPresent(ReactionDTO reactionDTO) {
        logger.info("Checking if reaction exists");
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "select exists (select 1 from public.\"Reaction\" where user_id = ? and comment_id = ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, reactionDTO.userId());
            statement.setLong(2, reactionDTO.commentId());
            ResultSet resultSet = statement.executeQuery();

            resultSet.next();
            Boolean isPresent = resultSet.getBoolean(1);
            logger.info("Checked if reaction exists");
            return new SuccessResponse<>(isPresent);
        } catch (SQLException e) {
            logger.severe("Failed to get existing reaction: " + e.getMessage() + " Sql state " + e.getSQLState());
            return new ErrorResponse<>("Failed to get existing reaction", ResponseStatus.InternalServerError);
        }
    }

    @Override
    @InjectLogger
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    //ok
    @Override
    public Response<ReactionDTO> create(ReactionDTO entity) {
        logger.info("Creating reaction");
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "insert into public.\"Reaction\" values (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, entity.userId());
            statement.setLong(2, entity.commentId());
            statement.setString(3, entity.reactionType().toString());

            int rows = statement.executeUpdate();
            if (rows != 1) {
                logger.warning("Failed to insert reaction into the database. Sql worked good");
                return new ErrorResponse<>("Failed to insert reaction", ResponseStatus.InternalServerError);
            }

            logger.info("Successfully inserted reaction");
            return new SuccessResponse<>(entity);
        } catch (SQLException e) {
            logger.severe("Failed to insert reaction: " + e.getMessage() + " Sql state " + e.getSQLState());
            return new ErrorResponse<>("Failed to insert reaction", ResponseStatus.InternalServerError);
        }
    }

    //ok
    @Override
    public Response<ReactionDTO> delete(ReactionDTO entity) {
        logger.info("Deleting reaction");
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "delete from public.\"Reaction\" where user_id = ? and comment_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, entity.userId());
            statement.setLong(2, entity.commentId());

            int rows = statement.executeUpdate();
            if (rows != 1) {
                logger.warning("Failed to delete reaction into the database. Sql worked good");
                return new ErrorResponse<>("Failed to delete reaction", ResponseStatus.InternalServerError);
            }
            logger.info("Successfully deleted reaction");
            return new SuccessResponse<>(entity);
        } catch (SQLException e) {
            logger.severe("Failed to delete reaction: " + e.getMessage() + " Sql state " + e.getSQLState());
            return new ErrorResponse<>("Failed to delete reaction", ResponseStatus.InternalServerError);
        }
    }

    @Override
    public Response<ReactionDTO> update(ReactionDTO entity) {
        logger.info("Updating reaction");
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "update public.\"Reaction\" set type = ? where user_id = ? and comment_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, entity.reactionType().toString());
            statement.setLong(2, entity.userId());
            statement.setLong(3, entity.commentId());

            int rows = statement.executeUpdate();

            if (rows != 1) {
                logger.warning("Failed to update reaction into the database. Sql worked good");
                return new ErrorResponse<>("Failed to update reaction", ResponseStatus.InternalServerError);
            }
            logger.info("Successfully updated reaction");
            return new SuccessResponse<>(entity);

        } catch (SQLException e) {
            logger.severe("Failed to update reaction: " + e.getMessage() + " Sql state " + e.getSQLState());
            return new ErrorResponse<>("Failed to update reaction", ResponseStatus.InternalServerError);
        }
    }

    @Override
    public Response<ReactionDTO> getById(ReactionDTO DTOid) {
        logger.info("Retrieving reaction");
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "select * from public.\"Reaction\" where user_id = ? and comment_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, DTOid.userId());
            statement.setLong(2, DTOid.commentId());

            ResultSet set = statement.executeQuery();
            if (!set.next()) {
                logger.warning("Failed to retrieve reaction into the database. Sql worked good");
                return new ErrorResponse<>("Failed to retrieve reaction", ResponseStatus.NotFound);
            }
            ReactionType type = set.getString("type").equals("LIKE") ? ReactionType.LIKE :
                    ReactionType.DISLIKE;
            logger.info("Successfully retrieved reaction");
            return new SuccessResponse<>(new ReactionDTO(DTOid.userId(), DTOid.commentId(), type));
        } catch (SQLException e) {
            logger.severe("Failed to retrieve reaction: " + e.getMessage() + " Sql state " + e.getSQLState());
            return new ErrorResponse<>("Failed to retrieve reaction", ResponseStatus.InternalServerError);
        }
    }
}
