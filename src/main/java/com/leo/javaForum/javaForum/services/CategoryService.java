package com.leo.javaForum.javaForum.services;


import com.leo.javaForum.javaForum.logger.Loggable;
import com.leo.javaForum.javaForum.models.DTOs.CategoryDTO;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService extends Loggable {
    Response<CategoryDTO> category(String categoryName);
    Response<List<CategoryDTO>> allCategories();
}
