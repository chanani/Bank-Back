package com.bank.gugu.global.exception;

import com.bank.gugu.global.exception.dto.ErrorCode;
import com.bank.gugu.global.exception.dto.ErrorResponse;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    // jakarta.validation 관련 예외 발생 처리
    @ExceptionHandler({ValidationException.class})
    protected ResponseEntity<ErrorResponse> handleValidationException(ValidationException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.warn("ValidationException exception occurred", exception);
        return ResponseEntity.status(status)
                .body(ErrorResponse.error(status.value(), exception.getMessage()));
    }

    // org.springframework.web.bind Valid 관련 예외 발생 처리
    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String message = e.getMessage();
        ObjectError error = e.getBindingResult().getAllErrors().stream().findFirst()
                .orElse(null);

        if (!ObjectUtils.isEmpty(error)) {
            message = error.getDefaultMessage();
        }

        log.error(e.getMessage(), e);
        return ResponseEntity.status(status)
                .body(ErrorResponse.error(status.value(), message));
    }

    // 비즈니스 관련 예외 발생 처리
    @ExceptionHandler({OperationErrorException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<ErrorResponse> handleOperationErrorException(OperationErrorException exception) {
        log.warn("Operation exception occurred", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.error(exception.getErrorCode()));
    }

    // 토큰 인증 UNAUTHORIZED(401) 예외 발생 처리
    @ExceptionHandler({AuthorizationException.class})
    protected ResponseEntity<ErrorResponse> handleAuthorizationException(AuthorizationException exception) {
        log.warn("Authorization exception occurred", exception);
        return toResponse(HttpStatus.UNAUTHORIZED, exception);
    }

    // 토큰 인증 FORBIDDEN(403) 예외 발생 처리
    @ExceptionHandler({ForbiddenException.class})
    protected ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException exception) {
        log.warn("Forbidden exception occurred", exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.error(exception.getErrorCode()));
    }

    // IllegalArgumentException 관련 예외 발생 처리
    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn("IllegalArgument exception occurred", exception);
        return toResponse(HttpStatus.BAD_REQUEST, exception);
    }

    // 예상치 못한 예외에 대한 처리
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.warn("Other exception occurred", exception);
        return toResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }

    // 파비콘 요청 무시에 대한 처리
    @ExceptionHandler({NoResourceFoundException.class})
    public ResponseEntity<Object> handleNoResource(NoResourceFoundException ex) {
        if ("favicon.ico".equals(ex.getResourcePath())) {
            return ResponseEntity.notFound().build();  // 조용히 무시
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("code", -1, "status", 500, "message", "No static resource " + ex.getResourcePath()));
    }

    // Response 객체 반환
    private ResponseEntity<ErrorResponse> toResponse(HttpStatus status, Exception exception) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.error(status.value(), exception.getMessage()));
    }

}