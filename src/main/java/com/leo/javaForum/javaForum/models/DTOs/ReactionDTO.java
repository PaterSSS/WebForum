package com.leo.javaForum.javaForum.models.DTOs;


import com.leo.javaForum.javaForum.models.DTOs.types.ReactionType;

public record ReactionDTO (Long userId, Long commentId, ReactionType reactionType) {
}
