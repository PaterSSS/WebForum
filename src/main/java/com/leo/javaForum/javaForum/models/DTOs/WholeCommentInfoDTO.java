package com.leo.javaForum.javaForum.models.DTOs;

public record WholeCommentInfoDTO(CommentDTO comment, ReactionsDTO likes, ReactionsDTO dislikes) {
}
