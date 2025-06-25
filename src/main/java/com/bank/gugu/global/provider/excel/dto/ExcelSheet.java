package com.bank.gugu.global.provider.excel.dto;

import java.util.List;

public record ExcelSheet(
        // 시트 명
        String name,
        // 시트 헤더
        List<String> headers,
        // 시트 로우
        List<List<Object>> rows
) {

}
