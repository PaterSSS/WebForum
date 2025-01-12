package com.leo.javaForum.javaForum.repositories;

import com.leo.javaForum.javaForum.logger.Loggable;
import com.leo.javaForum.javaForum.models.DTOs.CommentDTO;
import com.leo.javaForum.javaForum.models.DTOs.ReactionsDTO;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.CreateDBOperation;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.DeleteDBOperation;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.UpdateDBOperation;

import java.util.List;
import java.util.Map;

public interface CommentRepository extends CreateDBOperation<CommentDTO>,
        DeleteDBOperation<CommentDTO>, UpdateDBOperation<CommentDTO>, Loggable {
    Response<List<CommentDTO>> getAllCommentsInPost(Long postId);
    Response<Map<String , ReactionsDTO>> getReactionsOnComment(Long commentId);
    //если нажимает второй раз, то нужно удалить реакцию
    //нужен будет вспомогательный метод, который будет проверять ставил ли человек уже реакцию
    //от его результата отталкиваться, что вводить в sql

}
