package com.leo.javaForum.javaForum.services.implementation;


import com.leo.javaForum.javaForum.annotations.InjectLogger;
import com.leo.javaForum.javaForum.models.DTOs.UserDTO;
import com.leo.javaForum.javaForum.models.responseModel.ErrorResponse;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.models.responseModel.ResponseStatus;
import com.leo.javaForum.javaForum.repositories.UserRepository;
import com.leo.javaForum.javaForum.security.Encoder;
import com.leo.javaForum.javaForum.services.AuthentificationService;

import java.util.logging.Logger;

public class AuthintificationServImpl implements AuthentificationService {
    private Logger logger;
    private final UserRepository userRepository;

    public AuthintificationServImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Response<UserDTO> login(String username, String password) {
        logger.info("Login attempt");
        Response<UserDTO> response = userRepository.findByUsername(username);
        switch (response.getStatusCode()) {
            case ResponseStatus.OK -> {
                String dbPassword = response.getData().password();
                if (Encoder.verifyPassword(password, dbPassword)) {
                    logger.info("Successfully logged in");
                    return response;
                }
                logger.info("Invalid password");
                return new ErrorResponse<>("Incorrect password. Try again", ResponseStatus.NotFound);
            }
            case ResponseStatus.NotFound -> {
                return new ErrorResponse<>("Incorrect username. Try again", ResponseStatus.NotFound);
            }
            default -> {
                return response;
            }
        }
    }

    @Override
    public Response<UserDTO> register(String username, String password) {
        logger.info("Register attempt");
        Response<UserDTO> response = userRepository.findByUsername(username);
        switch (response.getStatusCode()) {
            case ResponseStatus.OK -> {
                logger.info("Username already exists");
                return new ErrorResponse<>("User with such username already exists. Try another",
                        ResponseStatus.CONFLICT);
            } case ResponseStatus.NotFound -> {
                logger.info("Trying to register a new user");
                String hashedPassword = Encoder.hashPassword(password);
                Response<UserDTO> testCreation = userRepository.create(new UserDTO(null, username, hashedPassword));
                if (testCreation.getStatusCode() == ResponseStatus.OK)
                    logger.info("Successfully registered");
                else logger.info("Registration failed");
                return testCreation;
            }
            default -> {
                logger.severe("Registration failed");
                return response;
            }
        }
    }

    @Override
    public Response<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @InjectLogger
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
