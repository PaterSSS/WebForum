package com.leo.javaForum.javaForum.models.DTOs;


import com.leo.javaForum.javaForum.models.DTOs.types.ReactionType;

public record ReactionsDTO(Long commentId, ReactionType type, int count) {
}
