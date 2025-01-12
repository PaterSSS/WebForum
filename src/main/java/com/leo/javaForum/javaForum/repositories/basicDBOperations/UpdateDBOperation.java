package com.leo.javaForum.javaForum.repositories.basicDBOperations;


import com.leo.javaForum.javaForum.models.responseModel.Response;

public interface UpdateDBOperation<T> {
    Response<T> update(T entity);
}
