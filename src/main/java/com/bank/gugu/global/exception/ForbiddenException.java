package com.bank.gugu.global.exception;


import com.bank.gugu.global.exception.dto.ErrorCode;

public class ForbiddenException extends AdviceBaseException {

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ForbiddenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
