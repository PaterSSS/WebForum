package com.leo.javaForum.javaForum.repositories;


import com.leo.javaForum.javaForum.logger.Loggable;
import com.leo.javaForum.javaForum.models.DTOs.CategoryDTO;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.ReadDBOperation;

import java.util.List;

public interface CategoryInfoRepository extends ReadDBOperation<CategoryDTO>, Loggable {
    Response<List<CategoryDTO>> getAllCategories();
}
