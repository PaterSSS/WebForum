package com.leo.javaForum.javaForum.repositories.basicDBOperations;


import com.leo.javaForum.javaForum.models.responseModel.Response;

public interface CreateDBOperation<T> {
    Response<T> create(T entity);
}
