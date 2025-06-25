package com.bank.gugu.global.exception;


import com.bank.gugu.global.exception.dto.ErrorCode;

public class OperationErrorException extends AdviceBaseException{

    public OperationErrorException(ErrorCode errorCode) {
        super(errorCode);
    }

    public OperationErrorException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
