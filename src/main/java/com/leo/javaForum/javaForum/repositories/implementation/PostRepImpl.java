package com.leo.javaForum.javaForum.repositories.implementation;


import com.leo.javaForum.javaForum.annotations.InjectLogger;
import com.leo.javaForum.javaForum.logger.AppLogger;
import com.leo.javaForum.javaForum.models.DTOs.PostDTO;
import com.leo.javaForum.javaForum.models.responseModel.ErrorResponse;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.models.responseModel.ResponseStatus;
import com.leo.javaForum.javaForum.models.responseModel.SuccessResponse;
import com.leo.javaForum.javaForum.repositories.PostRepository;
import com.leo.javaForum.javaForum.repositories.connetion.DatabaseConnector;
import com.leo.javaForum.javaForum.repositories.connetion.PostgresqlConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PostRepImpl implements PostRepository {
    private Logger logger;
    private final DatabaseConnector connector;

    public PostRepImpl(DatabaseConnector connector) {
        this.connector = connector;
    }

    @Override
    @InjectLogger
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    //ok
    @Override
    public Response<PostDTO> create(PostDTO entity) {
        logger.info("Creating a new post");
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "INSERT INTO public.\"Post\" (title, content, user_id, category_name) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, entity.title());
            statement.setString(2, entity.content());
            statement.setLong(3, entity.authorId());
            statement.setString(4, entity.categoryName());

            int row = statement.executeUpdate();
            if (row != 1) {
                logger.warning("Failed to insert new post");
                return new ErrorResponse<>("Failed to insert new post", ResponseStatus.InternalServerError);
            }

            logger.info("Successfully created a new post");
            return new SuccessResponse<>(entity);
        } catch (SQLException e) {
            logger.severe("Failed to create a new post: " + e.getMessage() + " : " + e.getSQLState());
            return new ErrorResponse<>("Failed to create a new post", ResponseStatus.InternalServerError);
        }

    }

    //удаление комментариев под постом и реакций на комменты происходит за счёт каскодного удаления в бд
    //ok
    @Override
    public Response<PostDTO> delete(PostDTO entity) {
        logger.info("Deleting a post with title: " + entity.title());
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "DELETE FROM public.\"Post\" WHERE post_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, entity.postId());
            int rows = statement.executeUpdate();
            if (rows != 1) {
                logger.warning("Not found post : " + entity.title() + " to delete.");
                return new ErrorResponse<>("Failed to delete a post", ResponseStatus.NotFound);
            }
            logger.info("Successfully deleted a post with id: " + entity.postId());
            return new SuccessResponse<>(entity);
        } catch (SQLException e) {
            logger.severe("Failed to delete a post: " + e.getMessage() + " : " + e.getSQLState());
            return new ErrorResponse<>("Failed to delete a post", ResponseStatus.InternalServerError);
        }
    }

    //ok
    @Override
    public Response<PostDTO> getById(PostDTO DTOid) {
        logger.info("Getting post by ID");
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "SELECT * FROM public.\"Post\" WHERE post_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, DTOid.postId());
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                logger.warning("No post found with ID: " + DTOid.postId());
                return new ErrorResponse<>("No post found with Id: " + DTOid.postId(), ResponseStatus.NotFound);
            }
            String title = resultSet.getString("title");
            String content = resultSet.getString("content");
            String creationDate = resultSet.getString("creation_date").substring(0, 10);
            Long authorId = resultSet.getLong("user_id");
            String categoryName = resultSet.getString("category_name");

            PostDTO dto = new PostDTO(DTOid.postId(), title, content, authorId, creationDate, categoryName);
            logger.info("Post found with ID: " + DTOid.postId());
            return new SuccessResponse<>(dto);
        } catch (SQLException e) {
            logger.severe("Error while getting post by ID: " + DTOid.postId() + e.getMessage() + " : " + e.getSQLState());
            return new ErrorResponse<>("Error while getting post.", ResponseStatus.InternalServerError);
        }
    }

    //ok
    @Override
    public Response<PostDTO> update(PostDTO entity) {
        logger.info("Updating a post with title: " + entity.title());
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "Update public.\"Post\" SET title = ?, content = ? WHERE post_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, entity.title());
            statement.setString(2, entity.content());
            statement.setLong(3, entity.postId());

            int rows = statement.executeUpdate();
            if (rows != 1) {
                logger.warning("Failed to update a post with title: " + entity.title());
                return new ErrorResponse<>("Failed to update a post", ResponseStatus.NotFound);
            }

            logger.info("Successfully updated a post with title: " + entity.title());
            return new SuccessResponse<>(entity);
        } catch (SQLException e) {
            logger.severe("Failed to update a post: " + e.getMessage() + " : " + e.getSQLState());
            return new ErrorResponse<>("Failed to update a post", ResponseStatus.InternalServerError);
        }
    }

    //ok
//    @Override
//    public Response<List<PostDTO>> getAllPostsInCategory(String category) {
//        logger.info("Getting all the posts in category: " + category);
//        Connection connection;
//        try {
//            connection = connector.getConnection();
//        } catch (RuntimeException e) {
//            logger.severe("Failed to access connection: " + e.getMessage());
//            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
//        }
//        String query = "SELECT * FROM public.\"Post\" WHERE category_name = ?";
//        try (PreparedStatement statement = connection.prepareStatement(query)) {
//            statement.setString(1, category);
//            ResultSet set = statement.executeQuery();
//
//            List<PostDTO> dtos = new ArrayList<>();
//            while (set.next()) {
//                Long postId = set.getLong("post_id");
//                String title = set.getString("title");
//                String content = set.getString("content");
//                String creationDate = set.getString("creation_date");
//                Long authorId = set.getLong("user_id");
//                dtos.add(new PostDTO(postId, title, content, authorId, creationDate, category));
//            }
//            if (dtos.isEmpty()) {
//                logger.warning("No posts were found in category " + category);
//                return new ErrorResponse<>("No posts were found in category " + category, ResponseStatus.NotFound);
//            }
//            logger.info("Found posts in category " + category);
//            return new SuccessResponse<>(dtos);
//        } catch (SQLException e) {
//            logger.severe("Error while getting all posts in category " + category + ". " + e.getMessage() +
//                    " : " + e.getSQLState());
//            return new ErrorResponse<>("Error while getting posts in category " + category,
//                    ResponseStatus.InternalServerError);
//
//        }
//    }
    //ok
    @Override
    public Response<List<PostDTO>> getPostsOnPage(String category, int offset, String sortType) {
        logger.info("Getting all posts on page number");
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "SELECT * from public.\"Post\" WHERE category_name = ? " +
                "ORDER BY creation_date " + sortType + " LIMIT ? OFFSET ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, category);
            statement.setInt(2, PostRepository.PAGE_SIZE);
            statement.setInt(3, offset);

            ResultSet set = statement.executeQuery();
            List<PostDTO> dtos = new ArrayList<>();
            while (set.next()) {
                Long postId = set.getLong("post_id");
                String title = set.getString("title");
                String content = set.getString("content");
                String creationDate = set.getString("creation_date").substring(0, 10);
                Long authorId = set.getLong("user_id");
                dtos.add(new PostDTO(postId, title, content, authorId, creationDate, category));
            }
            if (dtos.isEmpty()) {
                logger.warning("No posts were found in category " + category);
                return new ErrorResponse<>("No posts were found in category " + category, ResponseStatus.NotFound);
            }
            logger.info("Found posts in category " + category);
            return new SuccessResponse<>(dtos);

        } catch (SQLException e) {
            logger.severe("Failed to get posts on page in category: " + category + " "
                    + e.getMessage() + " : " + e.getSQLState());
            return new ErrorResponse<>("Failed to get posts on page", ResponseStatus.InternalServerError);
        }
    }

    //ok
    @Override
    public Response<Integer> countAllPostsInCategory(String category) {
        logger.info("Getting number of posts in category " + category);
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }
        String query = "SELECT COUNT(*) from public.\"Post\" where category_name = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, category);
            ResultSet set = statement.executeQuery();

            set.next();
            Integer count = set.getInt(1);
            return new SuccessResponse<>(count);
        } catch (SQLException e) {
            logger.severe("Failed to get number of posts in category " + category + " " +
                    e.getMessage() + " : " + e.getSQLState());
            return new ErrorResponse<>("Failed to count posts in category " + category, ResponseStatus.InternalServerError);
        }
    }

    public static void main(String[] args) {
        Logger logger1 = AppLogger.getLogger();
        DatabaseConnector connector1 = new PostgresqlConnector();
        connector1.setLogger(logger1);
        PostRepository repository = new PostRepImpl(connector1);
        repository.setLogger(logger1);
        var resp = repository.countAllPostsInCategory("Кино");
        System.out.println(resp.getData());
    }
}

