package com.bank.gugu.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 엑셀 다운로드시 사용하는 어노테이션
 * <pre>
 * response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(downloadFilename, StandardCharsets.UTF_8) + ".xlsx\"");
 * response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
 * </pre>
 * 다음 과 같은 현태로 response 에 헤더 및 contentType 을 설정한다. <br>
 * 컨트롤러에서 사용하며 해당 컨트롤러에서 엑셀 생성 후 byte[] 를 넘기면 됩니다.
 * <pre>
 * // 예시
 *\@ExcelDownload(fileName = "#name")
 *\@GetMapping("/download/excel")
 * public ResponseEntity<byte[]> downloadExcel(@RequestParam String name) {
 *    byte[] excelData = // 엑셀 생성 후 데이터 반환
 *    return ResponseEntity.ok(excelData);
 * }
 * </pre>
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelDownload {
    String fileName() default "downloadExcel.xlsx";
}
