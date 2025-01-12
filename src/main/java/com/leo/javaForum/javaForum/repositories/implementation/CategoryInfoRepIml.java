package com.leo.javaForum.javaForum.repositories.implementation;


import com.leo.javaForum.javaForum.annotations.InjectLogger;
import com.leo.javaForum.javaForum.models.DTOs.CategoryDTO;
import com.leo.javaForum.javaForum.models.responseModel.ErrorResponse;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.models.responseModel.ResponseStatus;
import com.leo.javaForum.javaForum.models.responseModel.SuccessResponse;
import com.leo.javaForum.javaForum.repositories.CategoryInfoRepository;
import com.leo.javaForum.javaForum.repositories.connetion.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

// можно вынести в отдельный метод проверку и подключение к базе данных, чтобы была проверка,
// мы ловили в случае чего ошибку и возвращались в сервис с ошибочным кодом ответа.
public class CategoryInfoRepIml implements CategoryInfoRepository {
    private final DatabaseConnector connector;

    private Logger logger;

    public CategoryInfoRepIml(DatabaseConnector connector) {
        this.connector = connector;
    }

    @Override
    public Response<CategoryDTO> getById(CategoryDTO DTOid) {
        logger.info("Getting category by name: " + DTOid);
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }

        String query = "SELECT description from public.\"category\" where category_name = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, DTOid.categoryName());
            ResultSet resultSet = statement.executeQuery();

            String description;

            if (!resultSet.next()) {
                logger.warning("No category found with name: " + DTOid);
                return new ErrorResponse<>("Category " + DTOid + " was not found",
                        ResponseStatus.NotFound);
            }

            description = Optional.ofNullable(resultSet.getString("description")).orElse("");
            logger.info("Got category with name: " + DTOid);
            CategoryDTO dto = new CategoryDTO(DTOid.categoryName(), description);
            return new SuccessResponse<>(dto);

        } catch (SQLException exception) {
            logger.severe("error while getting category by name: " + DTOid + " " +
                    exception.getMessage() + " " + exception.getSQLState());
            return new ErrorResponse<>("Error while getting category.", ResponseStatus.InternalServerError);
        }
    }

    @Override
    public Response<List<CategoryDTO>> getAllCategories() {
        logger.info("Getting all categories");
        Connection connection;
        try {
            connection = connector.getConnection();
        } catch (RuntimeException e) {
            logger.severe("Failed to access connection: " + e.getMessage());
            return new ErrorResponse<>("Failed to access database", ResponseStatus.InternalServerError);
        }

        String query = "select category_name, description from public.\"category\"";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            List<CategoryDTO> dtos = new ArrayList<>();

            while (resultSet.next()) {
                dtos.add(new CategoryDTO(resultSet.getString("category_name"),
                        Optional.ofNullable(resultSet.getString("description")).orElse("")));
            }

            if (dtos.isEmpty()) {
                logger.warning("No categories found");
                return new ErrorResponse<>("No categories found", ResponseStatus.NotFound);
            }

            logger.info("Got " + dtos.size() + " categories");
            return new SuccessResponse<>(dtos);

        } catch (SQLException exception) {
            logger.severe("error while getting all categories: " + exception.getMessage()
                    + " SQL state: " + exception.getSQLState());
            return new ErrorResponse<>("Error while getting all categories", ResponseStatus.InternalServerError);
        }
    }

    @Override
    @InjectLogger
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
