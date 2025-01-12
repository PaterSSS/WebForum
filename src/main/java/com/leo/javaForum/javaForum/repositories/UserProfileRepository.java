package com.leo.javaForum.javaForum.repositories;


import com.leo.javaForum.javaForum.logger.Loggable;
import com.leo.javaForum.javaForum.models.DTOs.PostDTO;
import com.leo.javaForum.javaForum.models.DTOs.UserProfileDTO;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.ReadDBOperation;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.UpdateDBOperation;

import java.util.List;

public interface UserProfileRepository extends ReadDBOperation<UserProfileDTO>,
        UpdateDBOperation<UserProfileDTO>, Loggable {
    Response<List<PostDTO>> allPostsByUserId(Long userId);
}
