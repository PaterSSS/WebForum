package com.leo.javaForum.javaForum.repositories.basicDBOperations;


import com.leo.javaForum.javaForum.models.responseModel.Response;

public interface ReadDBOperation<T> {
    Response<T> getById(T DTOid);
}
