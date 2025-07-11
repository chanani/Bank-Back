package com.bank.gugu.domain.recordsImage.service;

import com.bank.gugu.domain.record.repository.RecordsRepository;
import com.bank.gugu.domain.record.service.dto.response.RecordsMonthResponse;
import com.bank.gugu.domain.record.service.dto.response.RecordsResponse;
import com.bank.gugu.domain.recordsImage.repository.RecordsImageRepository;
import com.bank.gugu.domain.recordsImage.repository.condition.RecordImagesMonthCondition;
import com.bank.gugu.domain.recordsImage.service.response.RecordsImagesMonthResponse;
import com.bank.gugu.domain.recordsImage.service.response.RecordsImagesResponse;
import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.records.Records;
import com.bank.gugu.entity.recordsImage.RecordsImage;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.exception.OperationErrorException;
import com.bank.gugu.global.exception.dto.ErrorCode;
import com.bank.gugu.global.query.record.Range;
import com.bank.gugu.global.util.FileUtil;
import com.bank.gugu.global.util.dto.FileName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DefaultRecordsImageService implements RecordsImageService {

    private final RecordsImageRepository recordsImageRepository;
    private final RecordsRepository recordsRepository;
    private final FileUtil fileUtil;

    @Override
    @Transactional
    public void deleteRecordImage(Long recordsImageId) {
        // 이미지 조회
        RecordsImage findRecordImage = recordsImageRepository.findByIdAndStatus(recordsImageId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_RECORDS_IMAGE));
        // 이미지 소프트 삭제
        findRecordImage.remove();
    }

    @Override
    @Transactional
    public void addRecordImage(Long recordsId, MultipartFile inputFile, User user) {
        // 서버에 파일 등록
        FileName fileName = fileUtil.fileUpload(inputFile, "/bank/fileImage");
        // 입/출급 내역 조회
        Records findRecord = recordsRepository.findByIdAndStatus(recordsId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_RECORDS));
        // 데이터 등록
        RecordsImage newEntity = RecordsImage.builder()
                .records(findRecord)
                .path(fileName.getModifiedFileName())
                .user(user)
                .build();
        recordsImageRepository.save(newEntity);
    }

    @Override
    public List<RecordsImagesResponse> getRecordsImages(String date, User user) {
        // 해당 월의 첫 번째 날과 마지막 날 계산
        LocalDate startDate = LocalDate.parse(date.concat("-01"));
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // to condition
        RecordImagesMonthCondition condition = new RecordImagesMonthCondition(new Range(startDate, endDate), user);

        // 월별 이미지 목록 조회
        Map<LocalDate, List<RecordsImagesMonthResponse>> groupedByDate = recordsImageRepository.findMonthQuery(condition).stream()
                .collect(Collectors.groupingBy(RecordsImagesMonthResponse::getUseDate));

        return groupedByDate.entrySet().stream()
                .sorted(Map.Entry.<LocalDate, List<RecordsImagesMonthResponse>>comparingByKey().reversed())
                .map(entry -> new RecordsImagesResponse(
                        entry.getKey().toString(),
                        entry.getValue()
                ))
                .toList();
    }
}
