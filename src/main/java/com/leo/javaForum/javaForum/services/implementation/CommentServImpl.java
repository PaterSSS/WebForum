package com.leo.javaForum.javaForum.services.implementation;

import com.leo.javaForum.javaForum.annotations.InjectLogger;
import com.leo.javaForum.javaForum.logger.AppLogger;
import com.leo.javaForum.javaForum.models.DTOs.CommentDTO;
import com.leo.javaForum.javaForum.models.DTOs.ReactionDTO;
import com.leo.javaForum.javaForum.models.DTOs.ReactionsDTO;
import com.leo.javaForum.javaForum.models.DTOs.WholeCommentInfoDTO;
import com.leo.javaForum.javaForum.models.DTOs.types.ReactionType;
import com.leo.javaForum.javaForum.models.responseModel.ErrorResponse;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.models.responseModel.ResponseStatus;
import com.leo.javaForum.javaForum.models.responseModel.SuccessResponse;
import com.leo.javaForum.javaForum.repositories.CommentRepository;
import com.leo.javaForum.javaForum.repositories.ReactionRepository;
import com.leo.javaForum.javaForum.repositories.connetion.DatabaseConnector;
import com.leo.javaForum.javaForum.repositories.connetion.PostgresqlConnector;
import com.leo.javaForum.javaForum.repositories.implementation.CommentRepImpl;
import com.leo.javaForum.javaForum.repositories.implementation.ReactionRepImpl;
import com.leo.javaForum.javaForum.services.CommentService;
import com.leo.javaForum.javaForum.util.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CommentServImpl implements CommentService {
    private Logger logger;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;

    public CommentServImpl(CommentRepository commentRepository, ReactionRepository reactionRepository) {
        this.commentRepository = commentRepository;
        this.reactionRepository = reactionRepository;
    }

    @Override
    public Response<CommentDTO> addNewComment(CommentDTO comment) {
        logger.info("Adding new comment");
        if (comment == null || StrUtil.isInvalidStr(comment.content()) ||
                    comment.userId() <= 0 || comment.postId() <= 0) {
            logger.warning("Invalid comment");
            return new ErrorResponse<>("Invalid data for creating comment", ResponseStatus.BadRequest);
        }
        return commentRepository.create(comment);
    }

    @Override
    public Response<CommentDTO> deleteComment(CommentDTO comment) {
        logger.info("Deleting comment");
        if (comment == null || comment.id() <= 0 /* || userId <= 0 || comment.userId() <= 0*/) {
            logger.warning("Invalid comment");
            return new ErrorResponse<>("Invalid data for deleting comment", ResponseStatus.BadRequest);
        }
//        if (!comment.userId().equals(userId)) {
//            logger.warning("Invalid user id. Not enough permissions to delete comment");
//            return new ErrorResponse<>("Invalid data for deleting comment", ResponseStatus.BadRequest);
//        }
        return commentRepository.delete(comment);
    }

    @Override
    public Response<List<WholeCommentInfoDTO>> getAllCommentsWithReactions(Long postId) {
        if (postId <= 0) {
            logger.warning("Invalid post id: " + postId);
            return new ErrorResponse<>("Invalid post id", ResponseStatus.BadRequest);
        }
        Response<List<CommentDTO>> allComments = commentRepository.getAllCommentsInPost(postId);
        if (allComments.getStatusCode() == ResponseStatus.NotFound) {
            logger.warning("No comments in post with id: " + postId);
            return new ErrorResponse<>(allComments.getMessage(), ResponseStatus.NotFound);
        } else if (allComments.getStatusCode() == ResponseStatus.InternalServerError) {
            logger.severe("Internal server error while getting comments in post with id: " + postId);
            return new ErrorResponse<>(allComments.getMessage(), ResponseStatus.InternalServerError);
        }
        List<WholeCommentInfoDTO> result = new ArrayList<>();

        for (CommentDTO comment : allComments.getData()) {
            Map<String, ReactionsDTO> reactions = commentRepository.getReactionsOnComment(comment.id()).getData();
            if (!reactions.containsKey("LIKE")) {
                reactions.put("LIKE", new ReactionsDTO(comment.id(), ReactionType.LIKE, 0));
            }
            if (!reactions.containsKey("DISLIKE")) {
                reactions.put("DISLIKE", new ReactionsDTO(comment.id(), ReactionType.DISLIKE, 0));
            }
            WholeCommentInfoDTO commentInfoDTO = new WholeCommentInfoDTO(comment, reactions.get("LIKE"),
                    reactions.get("DISLIKE"));
            result.add(commentInfoDTO);
        }

        return new SuccessResponse<>(result);
    }

    @Override
    public Response<ReactionDTO> reactOnComment(ReactionDTO reaction) {
        logger.info("Leaving reaction");
        if (reaction == null || reaction.userId() <= 0 || reaction.commentId() <= 0) {
            logger.warning("Invalid data for leaving reaction: " + reaction);
            return new ErrorResponse<>("Invalid data for reaction", ResponseStatus.BadRequest);
        }
        Response<Boolean> isPresentInDB = reactionRepository.isPresent(reaction);
        if (isPresentInDB.getStatusCode() != ResponseStatus.OK) {
            return new ErrorResponse<>(isPresentInDB.getMessage(), ResponseStatus.InternalServerError);
        }
        Response<ReactionDTO> result;
        if (isPresentInDB.getData()) {
            result = reactionRepository.getById(reaction);

            if (result.getData().reactionType() == reaction.reactionType()) result = reactionRepository.delete(reaction);
            else result = reactionRepository.update(reaction);
        }
        else result = reactionRepository.create(reaction);

        return result;
    }

    @Override
    public Response<CommentDTO> updateComment(CommentDTO commentDTO) {
        return commentRepository.update(commentDTO);
    }

    @Override
    @InjectLogger
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public static void main(String[] args) {
        Logger logger1 = AppLogger.getLogger();
        DatabaseConnector connector = new PostgresqlConnector();
        connector.setLogger(logger1);
        CommentRepository commentRepository1 = new CommentRepImpl(connector);
        commentRepository1.setLogger(logger1);
        ReactionRepository reactionRepository = new ReactionRepImpl(connector);
        reactionRepository.setLogger(logger1);
        CommentService commentService = new CommentServImpl(commentRepository1, reactionRepository);
        commentService.setLogger(logger1);
        var response = commentService.reactOnComment(new ReactionDTO(1050L,6L, ReactionType.LIKE));
    }
}
