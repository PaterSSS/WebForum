package com.leo.javaForum.javaForum.models.DTOs;

//пока оставлю так, но можно сделать record, чтобы так много места не занимал код
public record UserProfileDTO(Long id, String username, String registrationDate, String bio) {

}
