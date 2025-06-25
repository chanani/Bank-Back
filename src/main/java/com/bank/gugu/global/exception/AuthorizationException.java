package com.bank.gugu.global.exception;


import com.bank.gugu.global.exception.dto.ErrorCode;

public class AuthorizationException extends AdviceBaseException {

    public AuthorizationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthorizationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
