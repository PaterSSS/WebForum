package com.leo.javaForum.javaForum.models.responseModel;

public interface Response<T> {
    T getData();

    String getMessage();

    ResponseStatus getStatusCode();
}
