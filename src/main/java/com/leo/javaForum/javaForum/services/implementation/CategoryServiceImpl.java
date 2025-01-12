package com.leo.javaForum.javaForum.services.implementation;

import com.leo.javaForum.javaForum.annotations.InjectLogger;
import com.leo.javaForum.javaForum.models.DTOs.CategoryDTO;
import com.leo.javaForum.javaForum.models.responseModel.ErrorResponse;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.models.responseModel.ResponseStatus;
import com.leo.javaForum.javaForum.repositories.CategoryInfoRepository;
import com.leo.javaForum.javaForum.services.CategoryService;

import java.util.List;
import java.util.logging.Logger;

public class CategoryServiceImpl implements CategoryService {
    private final CategoryInfoRepository repository;

    private Logger logger;

    public CategoryServiceImpl(CategoryInfoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Response<CategoryDTO> category(String categoryName) {
        logger.info("Category name: " + categoryName);
        if (categoryName == null || categoryName.isEmpty()) {
            logger.warning("Category name is null or empty");
            return new ErrorResponse<>("Incorrect name of category", ResponseStatus.BadRequest);
        }
        return repository.getById(new CategoryDTO(categoryName,null));
    }

    @Override
    public Response<List<CategoryDTO>> allCategories() {
        logger.info("All categories");
        return repository.getAllCategories();
    }

    @Override
    @InjectLogger
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
