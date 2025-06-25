package com.bank.gugu.global.page;

import lombok.Builder;

/**
 * 페이징 관련 전달 dto 입니다 <br>
 * page : 페이지 <br>
 * size : 한번에 가져올 아이템 수 <br>
 * sort : 기준 정렬 ex) createdAt : 생성일기준 asc 정렬 / -createdAt : 생성일기준 desc 정렬
 */
@Builder
public record PageInput(
        int page,
        int size,
        String sort
) {

}
