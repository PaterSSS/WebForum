package com.leo.javaForum.javaForum.services;


import com.leo.javaForum.javaForum.logger.Loggable;
import com.leo.javaForum.javaForum.models.DTOs.PostDTO;
import com.leo.javaForum.javaForum.models.DTOs.UserDTO;
import com.leo.javaForum.javaForum.models.DTOs.UserProfileDTO;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.ReadDBOperation;

import java.util.List;

public interface UserService extends ReadDBOperation<UserDTO>,  Loggable {
    Response<UserProfileDTO> getUserProfile(Long id);
    Response<UserProfileDTO> updateUserBio(UserProfileDTO user);
    Response<List<PostDTO>> getAllUserPosts(Long id);
}
