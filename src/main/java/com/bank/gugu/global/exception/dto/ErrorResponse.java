package com.bank.gugu.global.exception.dto;

public record ErrorResponse(int code, int status, String message) {

    public static ErrorResponse error(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getStatus(), errorCode.getMessage());
    }

    public static ErrorResponse error(int status, String message) {
        return new ErrorResponse(-1, status, message);
    }

}
