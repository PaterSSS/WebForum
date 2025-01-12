package com.leo.javaForum.javaForum.repositories.basicDBOperations;


import com.leo.javaForum.javaForum.models.responseModel.Response;

public interface DeleteDBOperation<T> {
    Response<T> delete(T entity);
}
