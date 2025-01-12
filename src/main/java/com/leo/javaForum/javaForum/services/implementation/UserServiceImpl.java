package com.leo.javaForum.javaForum.services.implementation;



import com.leo.javaForum.javaForum.annotations.InjectLogger;
import com.leo.javaForum.javaForum.models.DTOs.PostDTO;
import com.leo.javaForum.javaForum.models.DTOs.UserDTO;
import com.leo.javaForum.javaForum.models.DTOs.UserProfileDTO;
import com.leo.javaForum.javaForum.models.responseModel.ErrorResponse;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.models.responseModel.ResponseStatus;
import com.leo.javaForum.javaForum.repositories.UserProfileRepository;
import com.leo.javaForum.javaForum.repositories.UserRepository;
import com.leo.javaForum.javaForum.services.UserService;

import java.util.List;
import java.util.logging.Logger;

public class UserServiceImpl implements UserService {
    private final UserProfileRepository profileRepository;
    private final UserRepository userRepository;
    private Logger logger;

    public UserServiceImpl(UserProfileRepository repository, UserRepository userRepository) {
        this.profileRepository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public Response<UserProfileDTO> getUserProfile(Long id) {
        logger.info("getting user profile");
        if (id < 0) {
            logger.warning("Negative id");
            return new ErrorResponse<>("Incorrect id", ResponseStatus.BadRequest);
        }
        return profileRepository.getById(new UserProfileDTO(id, null,null,null));
    }

    @Override
    public Response<UserProfileDTO> updateUserBio(UserProfileDTO userProfile) {
        logger.info("updating user bio");
        if (userProfile == null || userProfile.id() < 0) {
            logger.warning("Incorrect id");
            return new ErrorResponse<>("Incorrect id", ResponseStatus.BadRequest);
        }
        return profileRepository.update(userProfile);
    }

    @Override
    public Response<List<PostDTO>> getAllUserPosts(Long id) {
        return profileRepository.allPostsByUserId(id);
    }

    @Override
    @InjectLogger
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Response<UserDTO> getById(UserDTO DTOid) {
        return userRepository.getById(DTOid);
    }
}
