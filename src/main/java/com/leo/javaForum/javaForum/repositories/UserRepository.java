package com.leo.javaForum.javaForum.repositories;


import com.leo.javaForum.javaForum.logger.Loggable;
import com.leo.javaForum.javaForum.models.DTOs.UserDTO;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.CreateDBOperation;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.ReadDBOperation;

public interface UserRepository extends ReadDBOperation<UserDTO>,
        CreateDBOperation<UserDTO>, Loggable {
    Response<UserDTO> findByUsername(String username);
}
