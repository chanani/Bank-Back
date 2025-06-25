package com.bank.gugu.global.util;

import com.bank.gugu.global.exception.OperationErrorException;
import com.bank.gugu.global.exception.dto.ErrorCode;
import com.bank.gugu.global.util.dto.FileName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

        if (!file.exists()) {
            try {
                file.mkdirs();  // 경로가없는경우 경로생성(없는 상위 디렉토리들을 생성함), 43행의 TransferTo(file)이 온전히 작동되게 하기 위한 코드.
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            inputFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new FileName(fileModify.substring(fileModify.indexOf("uploads/") + 8), Normalizer.normalize(originalFileName, Normalizer.Form.NFC));
    }

    /**
     * 파일 다건 업로드
     *
     * @param inputFileList 업로드할 파일 리스트
     * @param folderName    업로드할 폴더
     * @return FileName<원본 파일명, 업로드 파일명> 리스트
     */
    public List<FileName> fileListUpload(List<MultipartFile> inputFileList, String folderName) {
        List<FileName> FileNames = new ArrayList<>();
        for (MultipartFile inputFile : inputFileList) {
            FileNames.add(fileUpload(inputFile, folderName));
        }
        return FileNames;
    }

    /**
     * 파일 확장자 추출 메서드
     * @param fileName 원본 파일명
     * @return 파일 확장자
     */
    public String fileExtension(String fileName){
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }


    /**
     * 이미지 파일 확장자 체크
     * @param file 파일 객체
     */
    public void existsImageFileExtension(MultipartFile file) {
        String extension = fileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!extension.equals("png") &&
                !extension.equals("jpeg") &&
                !extension.equals("jpg") &&
                !extension.equals("webp")
        ) {
            throw new OperationErrorException(ErrorCode.EXISTS_EXTENSION);
        }
    }



}
