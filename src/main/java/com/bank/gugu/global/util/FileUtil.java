package com.bank.gugu.global.util;

import com.bank.gugu.global.exception.OperationErrorException;
import com.bank.gugu.global.exception.dto.ErrorCode;
import com.bank.gugu.global.util.dto.CustomMultipartFile;
import com.bank.gugu.global.util.dto.FileName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileUtil {

    @Value("${file.path}")
    private String ROOT_DIR;

    /**
     * 파일 단건 업로드
     *
     * @param inputFile  업로드할 파일
     * @param folderName 업로드할 파일 경로
     * @return FileName<원본 파일명, 업로드 파일명>
     */
    public FileName fileUpload(MultipartFile inputFile, String folderName) {
        // 빈 파일객체를 전달받은 경우 공백을 채워 리턴함.
        if (inputFile.isEmpty()) {
            return new FileName("", "");
        }

        // properties파일에서 지정한 ROOT_DIR 경로로 업로드 후 FileNames 객체를 리턴 하는 메서드
        // 타임스탬프 + 난수
        String UPLOAD_DIR = ROOT_DIR + folderName + "/";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());

        String originalFileName = inputFile.getOriginalFilename();

        int randomNum = (int) (Math.random() * 100);

        int lastDotIdx = originalFileName.lastIndexOf(".");
        String modifiedFileName = originalFileName.substring(0, lastDotIdx) + "(" + timestamp + ")" + randomNum + originalFileName.substring(lastDotIdx);
        String fileModify = UPLOAD_DIR + Normalizer.normalize(modifiedFileName, Normalizer.Form.NFC);

        File file = new File(fileModify);

        // 파일의 부모 디렉토리만 생성 (파일 자체는 생성하지 않음)
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            try {
                parentDir.mkdirs();  // 상위 디렉토리들만 생성
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            inputFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new FileName(fileModify, Normalizer.normalize(originalFileName, Normalizer.Form.NFC));
    }

    /**
     * 파일 다건 업로드
     *
     * @param inputFileList 업로드할 파일 리스트
     * @param folderName    업로드할 폴더
     * @return FileName<원본 파일명, 업로드 파일명> 리스트
     */
    public List<FileName> filesUpload(List<MultipartFile> inputFileList, String folderName) {
        List<FileName> FileNames = new ArrayList<>();
        for (MultipartFile inputFile : inputFileList) {
            FileNames.add(fileUpload(inputFile, folderName));
        }
        return FileNames;
    }

    /**
     * 파일 확장자 추출 메서드
     *
     * @param fileName 원본 파일명
     * @return 파일 확장자
     */
    public String fileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }


    /**
     * 이미지 파일 확장자 체크
     *
     * @param file 파일 객체
     */
    public void existsImageFileExtension(MultipartFile file) {
        String extension = fileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!extension.equals("png") &&
                !extension.equals("jpeg") &&
                !extension.equals("jpg")
        ) {
            throw new OperationErrorException(ErrorCode.EXISTS_EXTENSION);
        }
    }

    public MultipartFile resizeImage(MultipartFile originalFile) throws IOException {
        // 원본 파일을 InputStream으로 읽기
        InputStream inputStream = originalFile.getInputStream();

        // ByteArrayOutputStream을 사용해 리사이징된 이미지 저장
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Thumbnailator를 사용한 리사이징
        Thumbnails.of(inputStream)
                .size(800, 600)
                .outputFormat("jpg")
                .outputQuality(0.9f)  // 품질 80%
                .toOutputStream(outputStream);

        // 리사이징된 이미지를 MultipartFile로 변환
        byte[] resizedImageBytes = outputStream.toByteArray();

        // 새로운 파일명 생성 (원본 파일명 + _resized)
        String originalFileName = originalFile.getOriginalFilename();
        String fileName = originalFileName != null ?
                originalFileName.substring(0, originalFileName.lastIndexOf('.')) + ".jpg" :
                "resized_image.jpg";
        // CustomMultipartFile 생성 (아래 클래스 참고)
        return new CustomMultipartFile(
                resizedImageBytes,
                originalFile.getName(),
                fileName,
                "image/jpeg"
        );
    }


}
