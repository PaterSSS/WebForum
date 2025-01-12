package com.leo.javaForum.javaForum.models.responseModel;

public enum ResponseStatus {
    OK(200),
    NotFound(404),
    BadRequest(400),
    CONFLICT(409),
    InternalServerError(500);

    private final int code;

    ResponseStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
