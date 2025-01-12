package com.leo.javaForum.javaForum.models.DTOs;

//подумать о record
public record PostDTO(Long postId, String title, String content, Long authorId, String creationDate, String categoryName) {
}
