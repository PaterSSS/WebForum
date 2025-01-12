package com.leo.javaForum.javaForum.repositories;


import com.leo.javaForum.javaForum.logger.Loggable;
import com.leo.javaForum.javaForum.models.DTOs.ReactionDTO;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.CreateDBOperation;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.DeleteDBOperation;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.ReadDBOperation;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.UpdateDBOperation;

public interface ReactionRepository extends CreateDBOperation<ReactionDTO>,
        UpdateDBOperation<ReactionDTO>, DeleteDBOperation<ReactionDTO>, ReadDBOperation<ReactionDTO>, Loggable {
    Response<Boolean> isPresent(ReactionDTO reactionDTO);
}
