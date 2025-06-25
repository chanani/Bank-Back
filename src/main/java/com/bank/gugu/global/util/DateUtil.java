package com.bank.gugu.global.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    /**
     * 19990101 과 같은 생년월일 형식을 LocalDate로 변환합니다.
     * @param birth 19990101과 같은 형식
     * @return LocalDate
     * */
    public static LocalDate stringToBirthDate(String birth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(birth, formatter);
    }

    /**
     * LocalDateTime 형식을 문자열 형식의 HH:mm 으로 파싱
     * @param localDateTime LocalDateTime
     * @return HH:mm 형식의 문자열
     * */
    public static String localDateTimeToTimeString(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /**
     * LocalDateTime 형식을 문자열 형식의 HH:mm 으로 파싱
     * @param localDateTime LocalDateTime
     * @return yyyy-MM-dd hh:mm 형식의 문자열
     * */
    public static String localDateTimeTolocalDateTimeString(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
    }

    /**
     * 해당 타겟 날짜가 범위 사이의 값 인지 여부
     * @param start 시작 날짜
     * @param end 종료 날짜
     * @param target 타겟 날짜
     * @return true: 범위 사이에 존재, false: 범위 사이에 존재하지 않음
     * */
    public static boolean isBetween(LocalDateTime start, LocalDateTime end, LocalDateTime target) {
        return (target.isAfter(start) || target.isEqual(start)) && (target.isBefore(end) || target.isEqual(end));
    }

}
