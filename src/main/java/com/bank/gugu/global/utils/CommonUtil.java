package com.bank.gugu.global.utils;


import java.util.UUID;

public class CommonUtil {

    /**
     * 임의의 닉네임 생성
     * @return 영소문자, 숫자를 섞은 임의의 9글자
     */
    public static String createNickName() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // UUID에서 앞 9자리만 사용하고 소문자로 변환
        return uuid.substring(0, 9).toLowerCase();
    }


}
