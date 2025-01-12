package com.leo.javaForum.javaForum.models.DTOs;

public record CommentDTO(Long id, String content, String creationDate, Long postId, Long userId) {
}