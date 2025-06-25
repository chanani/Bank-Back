package com.bank.gugu.global.response;

public record ApiResponse(int code, int status, String message) {
    public static ApiResponse ok() {
        return new ApiResponse(200, 200, "SUCCESS");
    }

}
