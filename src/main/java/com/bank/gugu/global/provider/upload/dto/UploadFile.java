package com.bank.gugu.global.provider.upload.dto;

import lombok.Getter;

/**
 * ex. <br>
 * file : example.png / 12KB <br>
 * baseUrl : http://example.com/cdn <br>
 * sftp.upload.file-path : /opt/uploads/ <br>
 * 반환되는 UploadFile 값 <br>
 * <pre>
 *     {
 *         originalFileName : "example.png",
 *         fileName : "새로변경된파일명.png",
 *         pathname : "/opt/uploads/새로변경된파일명.png",
 *         size : 12000
 *         fileType : "image/png"
 *         bucket : "/uploads/새로변경된파일명.png"
 *     }
 * </pre>
 * 실제 저장된 곳은 /opt/uploads 지만 설정으로 인해 이미지를 불러올땐 <br>
 * sftp 서버에서 사전에 설정된 ex(/uploads/) 로 불러온다 <br>
 * 즉 http://example.com/cdn/opt/uploads/새로변경된파일명.png (x) <br>
 * http://example.com/cdn/uploads/새로변경된파일명.png (o) <br>
 * */
@Getter
public class UploadFile {
    // 원본 파일 명
    private String originalFileName;
    // 저장된 파일 명
    private String fileName;
    // 실제 저장된 파일 pathname
    private String pathname;
    // 파일 크기
    private int size;
    // 파일 유형
    private String fileType;
    // 버킷 (실제 불러올때 사용될 경로)
    private String bucket;
}
