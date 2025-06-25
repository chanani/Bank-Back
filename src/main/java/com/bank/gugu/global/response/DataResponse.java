package com.bank.gugu.global.response;

/**
 * RestController 에서 리스트만 반환하는 응답에 대한 객체
 * */
public record DataResponse<T>(T data) {
    public static <T>DataResponse<T> send(T data) {
        return new DataResponse<T>(data);
    }
    
}
