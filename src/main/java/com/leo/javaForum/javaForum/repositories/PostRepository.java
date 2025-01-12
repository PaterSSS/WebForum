package com.leo.javaForum.javaForum.repositories;

import com.leo.javaForum.javaForum.logger.Loggable;
import com.leo.javaForum.javaForum.models.DTOs.PostDTO;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.CreateDBOperation;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.DeleteDBOperation;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.ReadDBOperation;
import com.leo.javaForum.javaForum.repositories.basicDBOperations.UpdateDBOperation;

import java.util.List;

//если всё таки реализую отображения постов на которые подписался человек и которые сам сделал
//то нужно будет добавить метод поиска по user_id.
public interface PostRepository extends ReadDBOperation<PostDTO>, UpdateDBOperation<PostDTO>,
        CreateDBOperation<PostDTO>, DeleteDBOperation<PostDTO>, Loggable {
    Integer PAGE_SIZE = 5;
    Response<List<PostDTO>> getPostsOnPage(String category, int offset, String sortType);
    Response<Integer> countAllPostsInCategory(String category);
}
