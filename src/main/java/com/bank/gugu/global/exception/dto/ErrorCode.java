package com.bank.gugu.global.exception.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

@Getter
public enum ErrorCode {

    /**
     * 예외상수 추가시 HttpStatus 관련 (로그인 및 토큰 관련 논외. FORBIDDEN,UNAUTHORIZED)
     * 클라이언트 API요청 잘못된 경우(데이터 타입이 안 맞는 등) → HttpStatus.BAD_REQUEST.value()
     * 요청은 잘 들어왔는데 유효성검사에 걸린 경우 등 → HttpStatus.BAD_REQUEST.value()
     */

    SUCCESS(200, HttpStatus.OK.value(), "Success"),

    /** 기본 에러 */
    BAD_REQUEST(400, HttpStatus.BAD_REQUEST.value(), "Bad Request"),
    NOT_FOUND(404, HttpStatus.NOT_FOUND.value(), "Not Found"),
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error"),

    // 1000 ~ 1049 공통부문 관련 예외
    INVALID_TIME_ZONE(1000, HttpStatus.BAD_REQUEST.value(), "timeZoneId가 확인되지 않습니다."),
    NOT_FOUND_USER_INFO(1001, HttpStatus.BAD_REQUEST.value(), "유저정보가 확인되지 않습니다."),
    FAILED_TO_WITHDRAWAL(1002, HttpStatus.BAD_REQUEST.value(), "회원 탈퇴에 실패하였습니다."),
    INVALID_SORT_FIELD(1003, HttpStatus.BAD_REQUEST.value(), "올바르지 않은 정렬 조건입니다."),
    NOT_EQUAL_WRITER(1004, HttpStatus.BAD_REQUEST.value(), "작성자만 수정 및 삭제 할 수 있습니다."),
    NOT_FOUND_COMPANY_INFO(1005, HttpStatus.BAD_REQUEST.value(), "기업정보가 확인되지 않습니다."),
    EXISTS_EXTENSION(1006, HttpStatus.BAD_REQUEST.value(), "파일 확장자를 확인해주세요."),
    NOT_FOUND_SOCIAL_PHONE(1007, HttpStatus.BAD_REQUEST.value(), "계정에 연락처가 존재하지 않습니다."),
    USED_SOCIAL_ID(1008, HttpStatus.BAD_REQUEST.value(), "이미 사용 중인 계정입니다."),
    NOT_FOUND_SOCIAL_ID(1009, HttpStatus.BAD_REQUEST.value(), "존재하지 않은 계정입니다."),

    // 1050 ~ 1099 토큰 관련 예외
    // UNAUTHORIZED:401(리프레시토큰요청필요할때) , FORBIDDEN:403(팅겨내야할때)
    ACCESS_TOKEN_NOT_FOUND(1050, HttpStatus.FORBIDDEN.value(), "Access Token이 없습니다. 재로그인 하시기 바랍니다."),
    ACCESS_TOKEN_SIG_WRONG(1051, HttpStatus.FORBIDDEN.value(), "Access Token이 잘못된 서명입니다. (해킹)"),
    ACCESS_TOKEN_EXPIRED(1052, HttpStatus.UNAUTHORIZED.value(), "Access Token이 만료되었습니다. 재로그인 하시기 바랍니다."),
    ACCESS_TOKEN_NOT_SUPPORT(1053, HttpStatus.FORBIDDEN.value(), "Access Token이 지원되지 않는 토큰 입니다. (해킹)"),
    ACCESS_TOKEN_WRONG(1054, HttpStatus.FORBIDDEN.value(), "Access Token이 토큰이 잘못되었습니다. (해킹)"),
    REFRESH_TOKEN_NOT_FOUND(1055, HttpStatus.FORBIDDEN.value(), "Refresh Token이 없습니다. 재로그인 하시기 바랍니다."),
    REFRESH_TOKEN_SIG_WRONG(1056, HttpStatus.FORBIDDEN.value(), "Refresh Token이 잘못된 서명입니다. (해킹)"),
    REFRESH_TOKEN_NOT_SUPPORT(1057, HttpStatus.FORBIDDEN.value(), "Refresh Token이 지원되지 않는 토큰 입니다. (해킹)"),
    REFRESH_TOKEN_EXPIRED(1058, HttpStatus.FORBIDDEN.value(), "만료되었습니다. 재로그인 하시기 바랍니다."),
    REFRESH_TOKEN_NO_SAME(1059, HttpStatus.FORBIDDEN.value(), "중복 로그인 또는 요청이 다릅니다. 재로그인 하시기 바랍니다."),
    REFRESH_TOKEN_WRONG(1060, HttpStatus.FORBIDDEN.value(), "Refresh Token이 토큰이 잘못되었습니다. (해킹)"),

    // 1100 ~ 1149 회원 관련 예외
    NOT_FOUND_USER(1100, HttpStatus.FORBIDDEN.value(), "찾을 수 없는 회원입니다."),
    NOT_EQUAL_ID_PASSWORD(1101, HttpStatus.FORBIDDEN.value(), "아이디 또는 패스워드를 확인하세요."),
    EXISTS_USER_ID(1102, HttpStatus.BAD_REQUEST.value(), "이미 사용중인 아아디입니다."),
    NOT_EQUAL_PASSWORD(1103, HttpStatus.BAD_REQUEST.value(), "두 비밀번호가 일치하지 않습니다."),

    // 1150 ~ 1199 아이콘 관련 예외
    NOT_FOUND_ICON(1150, HttpStatus.FORBIDDEN.value(), "찾을 수 없는 아이콘입니다."),

    // 1200 ~ 1249 아이콘 관련 예외
    NOT_FOUND_CATEGORY(1200, HttpStatus.FORBIDDEN.value(), "찾을 수 없는 카테고리입니다."),

    // 1250 ~ 1299 자산 관련 예외
    NOT_FOUND_ASSETS(1250, HttpStatus.FORBIDDEN.value(), "찾을 수 없는 자산입니다."),
    NOT_FOUND_ASSETS_DETAIL(1251, HttpStatus.FORBIDDEN.value(), "찾을 수 없는 자산입니다."),

    // 1300 ~ 1349 입/출금 관련 예외
    NOT_FOUND_RECORDS(1300, HttpStatus.FORBIDDEN.value(), "찾을 수 없는 입/출금 내역입니다."),
    NOT_FOUND_RECORDS_IMAGE(1301, HttpStatus.FORBIDDEN.value(), "찾을 수 없는 이미지입니다.");






    private final int code;
    private final int status;
    private final String message;

    ErrorCode(int code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public static List<ErrorCodeData> getErrorCodes() {
        return Arrays.stream(ErrorCode.values())
                .map(ErrorCodeData::error)
                .toList();
    }
}
