package com.bank.gugu.global.eventListener.file;

import com.bank.gugu.domain.recordsImage.repository.RecordsImageRepository;
import com.bank.gugu.domain.recordsImage.service.RecordsImageService;
import com.bank.gugu.entity.recordsImage.RecordsImage;
import com.bank.gugu.global.eventListener.file.dto.FileEventDto;
import com.bank.gugu.global.util.FileUtil;
import com.bank.gugu.global.util.dto.FileName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileEventListener {

    private final FileUtil fileUtil;
    private final RecordsImageRepository recordsImageRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void uploadFile(FileEventDto fileEventDto) throws IOException {
        List<MultipartFile> resizedFiles = new ArrayList<>();
        for (MultipartFile inputFile : fileEventDto.inputFiles()) {
            System.out.println("inputFile.getOriginalFilename() = " + inputFile.getOriginalFilename());
            // File size Resizing
            MultipartFile resizedFile = fileUtil.resizeImage(inputFile);
            resizedFiles.add(resizedFile);
        }

        // 서버에 이미지 업로드
        List<FileName> fileNames = fileUtil.filesUpload(resizedFiles, "fileImage");
        List<RecordsImage> recordsImages = new ArrayList<>();
        // recordsImage 테이블에 등록
        for (FileName fileName : fileNames) {
            RecordsImage recordsImage = RecordsImage.builder()
                    .user(fileEventDto.user())
                    .records(fileEventDto.records())
                    .path(fileName.getModifiedFileName())
                    .build();
            recordsImages.add(recordsImage);
        }

        // 등록
        recordsImageRepository.saveAll(recordsImages);
    }
}
