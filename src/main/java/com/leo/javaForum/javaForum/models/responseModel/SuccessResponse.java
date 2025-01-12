package com.leo.javaForum.javaForum.models.responseModel;

public class SuccessResponse<T> implements Response<T> {
    private final T data;

    public SuccessResponse(T data) {
        this.data = data;
    }

    @Override
    public T getData() {
        return this.data;
    }

    @Override
    public String getMessage() {
        return "";
    }

    @Override
    public ResponseStatus getStatusCode() {
        return ResponseStatus.OK;
    }
}
