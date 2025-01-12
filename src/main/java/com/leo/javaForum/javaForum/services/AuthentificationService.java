package com.leo.javaForum.javaForum.services;


import com.leo.javaForum.javaForum.logger.Loggable;
import com.leo.javaForum.javaForum.models.DTOs.UserDTO;
import com.leo.javaForum.javaForum.models.responseModel.Response;

public interface AuthentificationService extends Loggable {
    Response<UserDTO> login(String username, String password);
    Response<UserDTO> register(String username, String password);
    Response<UserDTO> findByUsername(String username);
}
