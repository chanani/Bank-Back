package com.bank.gugu.global.exception.dto;

public record ErrorCodeData(int code, String name, String message) {

    public static ErrorCodeData error(ErrorCode errorCode) {
        return new ErrorCodeData(errorCode.getCode(), errorCode.name(), errorCode.getMessage());
    }

    public static ErrorCodeData error(int code, String name, String message) {
        return new ErrorCodeData(code, name, message);
    }
}
