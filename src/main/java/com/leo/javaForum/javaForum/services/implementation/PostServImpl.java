package com.leo.javaForum.javaForum.services.implementation;

import com.leo.javaForum.javaForum.annotations.InjectLogger;
import com.leo.javaForum.javaForum.models.DTOs.PostDTO;
import com.leo.javaForum.javaForum.models.responseModel.ErrorResponse;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.models.responseModel.ResponseStatus;
import com.leo.javaForum.javaForum.models.responseModel.SuccessResponse;
import com.leo.javaForum.javaForum.repositories.PostRepository;
import com.leo.javaForum.javaForum.services.PostService;

import java.util.List;
import java.util.logging.Logger;

import static com.leo.javaForum.javaForum.util.StrUtil.isInvalidStr;


public class PostServImpl implements PostService {
    private Logger logger;
    private final PostRepository postRepository;

    public PostServImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

//    @Override
//    public Response<List<PostDTO>> getAllPostsInCategory(String category) {
//        logger.info("Get all posts");
//        if (category == null || category.isBlank()) {
//            logger.warning("Category is blank or null");
//            return new ErrorResponse<>("Invalid name of category", ResponseStatus.BadRequest);
//        }
//        return postRepository.getAllPostsInCategory(category);
//    }

    @Override
    public Response<List<PostDTO>> getAllPostsOnPage(String categoryName, int pageNumber, String sorType) {
        logger.info("getAllPostsOnPage in service");
        if (categoryName == null || categoryName.isBlank() || pageNumber <= 0) {
            logger.warning("Category is blank or null");
            return new ErrorResponse<>("Invalid name of category", ResponseStatus.BadRequest);
        }
        int offset = (pageNumber - 1) * PostRepository.PAGE_SIZE;
        String sortTypeSql = "DESC";
        if (sorType.equals("oldest")) sortTypeSql = "ASC";
        return postRepository.getPostsOnPage(categoryName, offset, sortTypeSql);
    }

    @Override
    public Response<Integer> countPostsInCategory(String categoryName) {
        logger.info("countPostsInCategory in service");
        if (categoryName == null || categoryName.isBlank()) {
            logger.warning("Bad data for countPostsInCategory" + categoryName);
            return new ErrorResponse<>("Bad data for countPostsInCategory", ResponseStatus.BadRequest);
        }

        return postRepository.countAllPostsInCategory(categoryName);
    }

    @Override
    public Response<Integer> countPagesInCategory(String categoryName) {
        logger.info("countPagesInCategory in service");
        if (categoryName == null || categoryName.isBlank()) {
            logger.warning("Bad data for countPagesInCategory" + categoryName);
            return new ErrorResponse<>("Bad data for countPagesInCategory", ResponseStatus.BadRequest);
        }

        var count = postRepository.countAllPostsInCategory(categoryName).getData();
        int pageCount = (int) Math.ceil((double) count / PostRepository.PAGE_SIZE);
        return new SuccessResponse<>(pageCount);
    }

    @Override
    public Response<PostDTO> getPost(Long id) {
        logger.info("Get post by id");
        if (id == null || id <= 0) {
            logger.warning("Post id is null");
            return new ErrorResponse<>("Invalid post id", ResponseStatus.BadRequest);
        }
        return postRepository.getById(new PostDTO(id,null,null,null,null,null));
    }

    @Override
    public Response<PostDTO> createPost(PostDTO post) {
        logger.info("Create post");
        if (post == null || isInvalidStr(post.title()) ||
                isInvalidStr(post.content()) ||
                isInvalidStr(post.categoryName()) ||
                post.authorId() <= 0) {
            logger.warning("Invalid post title or content");
            return new ErrorResponse<>("Invalid post title or content", ResponseStatus.BadRequest);
        }
        return postRepository.create(post);
    }

    @Override
    public Response<PostDTO> updatePost(PostDTO post) {
        logger.info("Update post");
        if (post == null || isInvalidStr(post.title()) || isInvalidStr(post.content())) {
            logger.warning("Invalid post title or content");
            return new ErrorResponse<>("Invalid post title or content", ResponseStatus.BadRequest);
        }
//        if (!userId.equals(post.authorId())) {
//            logger.warning("try to modify someone else's post");
//            return new ErrorResponse<>("Post authors id and users not equals", ResponseStatus.BadRequest);
//        }
        return postRepository.update(post);
    }


    @Override
    public Response<PostDTO> deletePost(Long id) {
        logger.info("Delete post by id");
        if (id == null || id <= 0) {
            logger.warning("Post id is null or negative");
            return new ErrorResponse<>("Invalid post id", ResponseStatus.BadRequest);
        }
//        if (!userId.equals(id)) {
//            logger.warning("try to delete someone else's post");
//            return new ErrorResponse<>("Post id and users not equals", ResponseStatus.BadRequest);
//        }
        return postRepository.delete(new PostDTO(id,null,null,null,null,null));
    }

    @Override
    @InjectLogger
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
