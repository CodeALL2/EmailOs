package com.org.emaildispatcher.httpcode;

public class ApiResponse {
    private final int code;
    private final String message;

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

