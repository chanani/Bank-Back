package com.bank.gugu.global.regex;

public class Regex {
    // 아이디
    public static final String ID = "^[A-Za-z0-9]{8,20}$";
    // 닉네임
    public static final String KICKNAME = "^[가-힣a-zA-Z0-9_]{2,12}$";
    // 휴대폰 번호 및 지역 번호 포함
    public static final String PHONE = "^010\\d{8}$";
    // 이메일 주소
    public static final String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    // 비밀번호: 8~20자, 숫자/영문/특수문자 중 2종류 이상 포함
    public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)|(?=.*[A-Za-z])(?=.*[!@#$%^&*])|(?=.*\\d)(?=.*[!@#$%^&*]).{8,20}$";
    // 한글 이름 (2~6자)
    public static final String KOREAN_NAME = "^[가-힣]{2,6}$";
    // 영문 이름 (First Last 형식, 대소문자)
    public static final String ENGLISH_NAME = "^[A-Z][a-z]+\\s[A-Z][a-z]+$";
    // 생년월일 (YYYY-MM-DD)
    public static final String BIRTHDAY = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";
    // 숫자만 (예: 주민등록번호 뒷자리 등)
    public static final String ONLY_NUMBER = "^\\d+$";
    // 우편번호 (5자리)
    public static final String POSTAL_CODE = "^\\d{5}$";
    // URL 형식
    public static final String URL = "^(http|https)://[^\\s$.?#].[^\\s]*$";
    // IPv4 주소
    public static final String IPV4 = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
    // 사업자등록번호 (XXX-XX-XXXXX)
    public static final String BUSINESS_NUMBER = "^\\d{3}-\\d{2}-\\d{5}$";
}
