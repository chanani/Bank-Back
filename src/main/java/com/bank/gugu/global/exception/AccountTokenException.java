package com.bank.gugu.global.exception;


import com.bank.gugu.global.exception.dto.ErrorCode;

public class AccountTokenException extends AdviceBaseException {
    public AccountTokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AccountTokenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
