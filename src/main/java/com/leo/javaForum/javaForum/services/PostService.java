package com.leo.javaForum.javaForum.services;


import com.leo.javaForum.javaForum.logger.Loggable;
import com.leo.javaForum.javaForum.models.DTOs.PostDTO;
import com.leo.javaForum.javaForum.models.responseModel.Response;

import java.util.List;

public interface
PostService extends Loggable {
    Response<List<PostDTO>> getAllPostsOnPage(String categoryName, int pageNumber, String sortType);
    Response<Integer> countPostsInCategory(String categoryName);
    Response<Integer> countPagesInCategory(String categoryName);
    Response<PostDTO> getPost(Long id);
    Response<PostDTO> createPost(PostDTO post);
    Response<PostDTO> updatePost(PostDTO post);
    Response<PostDTO> deletePost(Long id);
}
