package com.leo.javaForum.javaForum.models.responseModel;

public class ErrorResponse<T> implements Response<T> {
    private final String message;
    private final ResponseStatus status;

    public ErrorResponse(String message, ResponseStatus status) {
        this.message = message;
        this.status = status;
    }

    @Override
    public T getData() {
        return null;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public ResponseStatus getStatusCode() {
        return this.status;
    }
}
