package com.leo.javaForum.javaForum.services;


import com.leo.javaForum.javaForum.logger.Loggable;
import com.leo.javaForum.javaForum.models.DTOs.CommentDTO;
import com.leo.javaForum.javaForum.models.DTOs.ReactionDTO;
import com.leo.javaForum.javaForum.models.DTOs.WholeCommentInfoDTO;
import com.leo.javaForum.javaForum.models.responseModel.Response;

import java.util.List;

public interface CommentService extends Loggable {
    Response<CommentDTO> addNewComment(CommentDTO comment);
    Response<CommentDTO> deleteComment(CommentDTO comment);
    Response<List<WholeCommentInfoDTO>> getAllCommentsWithReactions(Long postId);
    Response<ReactionDTO> reactOnComment(ReactionDTO reaction);
    Response<CommentDTO> updateComment(CommentDTO commentDTO);
}
