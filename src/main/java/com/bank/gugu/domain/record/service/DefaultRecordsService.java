package com.bank.gugu.domain.record.service;

import com.bank.gugu.domain.assets.repository.AssetsRepository;
import com.bank.gugu.domain.assetsDetail.repository.AssetsDetailRepository;
import com.bank.gugu.domain.category.repository.CategoryRepository;
import com.bank.gugu.domain.record.repository.RecordsRepository;
import com.bank.gugu.domain.record.repository.condition.RecordCalendarCondition;
import com.bank.gugu.domain.record.repository.condition.RecordCurrentCondition;
import com.bank.gugu.domain.record.repository.condition.RecordMonthCondition;
import com.bank.gugu.domain.record.service.dto.request.RecordCreateRequest;
import com.bank.gugu.domain.record.service.dto.request.RecordUpdateMemoRequest;
import com.bank.gugu.domain.record.service.dto.request.RecordUpdateRequest;
import com.bank.gugu.domain.record.service.dto.response.*;
import com.bank.gugu.domain.recordsImage.repository.RecordsImageRepository;
import com.bank.gugu.domain.user.repository.UserRepository;
import com.bank.gugu.entity.BaseEntity;
import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.assetsDetail.AssetsDetail;
import com.bank.gugu.entity.category.Category;
import com.bank.gugu.entity.common.constant.BooleanYn;
import com.bank.gugu.entity.common.constant.RecordType;
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
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DefaultRecordsService implements RecordsService {

    private final RecordsRepository recordsRepository;
    private final UserRepository userRepository;
    private final AssetsRepository assetsRepository;
    private final CategoryRepository categoryRepository;
    private final AssetsDetailRepository assetsDetailRepository;
    private final FileUtil fileUtil;
    private final RecordsImageRepository recordsImageRepository;

    @Override
    @Transactional
    public void addRecord(RecordCreateRequest request, User user, List<MultipartFile> inputFiles) throws IOException {
        // 카테고리 조회
        Category findCategory = categoryRepository.findByIdAndStatus(request.categoryId(), StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_CATEGORY));
        // assets entity 조회
        Assets findAssets = null;
        if (request.assetsId() != null && request.assetsId() > 0) {
            findAssets = assetsRepository.findByIdAndStatus(request.assetsId(), StatusType.ACTIVE)
                    .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ASSETS));
        }
        // dto -> entity
        Records newEntity = request.toEntity(user, findCategory, findAssets);
        // 등록
        Records saveRecord = recordsRepository.save(newEntity);

        // 자산 그룹을 지정했을 경우 자산 상세 내용 등록
        if (request.assetsId() != null && request.assetsId() > 0) {
            // dto -> assets Entity
            AssetsDetail newAssetsDetailEntity = request.toAssetsDetail(user, findAssets, saveRecord, findCategory);
            // 등록
            assetsDetailRepository.save(newAssetsDetailEntity);
            // 자산 그룹 합계 금액 업데이트
            findAssets.updateBalance(newAssetsDetailEntity);
        }


        if (inputFiles != null && !inputFiles.isEmpty()) {
            // 리사이징된 파일들을 저장할 리스트
            List<MultipartFile> resizedFiles = new ArrayList<>();

            // 확장자 체크(png, jpg, jpeg)
            for (MultipartFile inputFile : inputFiles) {
                fileUtil.existsImageFileExtension(inputFile);

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
                        .user(user)
                        .records(saveRecord)
                        .path(fileName.getModifiedFileName())
                        .build();
                recordsImages.add(recordsImage);
            }

            // 등록
            recordsImageRepository.saveAll(recordsImages);
        }
    }

    @Override
    @Transactional
    public void deleteRecord(Long recordsId) {
        // record Entity 조회
        Records findRecord = recordsRepository.findByIdAndStatus(recordsId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_RECORDS));
        // 소프트 삭제
        findRecord.remove();

        // 자산에 등록 되어있을 경우 내역 삭제 및 금액 차감
        if (findRecord.getAssets() != null) {
            assetsRepository.findByIdAndStatus(findRecord.getAssets().getId(), StatusType.ACTIVE)
                    .ifPresent(findAssets -> findAssets.removeBalance(findRecord));
            assetsDetailRepository.findByRecordIdAndStatus(findRecord.getId(), StatusType.ACTIVE)
                    .ifPresent(BaseEntity::remove);
        }
    }

    @Override
    @Transactional
    public void updateRecord(RecordUpdateRequest request, Long recordsId, List<MultipartFile> inputFiles, User user) throws IOException {
        // 카테고리 조회
        Category findCategory = categoryRepository.findByIdAndStatus(request.categoryId(), StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_CATEGORY));

        // assets entity 조회
        Assets findAssets = null;
        if (request.assetsId() != null && request.assetsId() > 0) {
            findAssets = assetsRepository.findByIdAndStatus(request.assetsId(), StatusType.ACTIVE)
                    .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ASSETS));
        }

        // dto -> entity
        Records newEntity = request.toEntity(findCategory, findAssets);

        // record Entity 조회
        Records findRecord = recordsRepository.findByIdAndStatus(recordsId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_RECORDS));

        // 자산 내역 수정
        if (findRecord.getAssets() != null) {
            assetsRepository.findByIdAndStatus(findRecord.getAssets().getId(), StatusType.ACTIVE)
                    .ifPresent(assets -> assets.updateRecordBalance(findRecord, newEntity));
            assetsDetailRepository.findByRecordIdAndStatus(findRecord.getId(), StatusType.ACTIVE)
                    .ifPresent(assetsDetail -> assetsDetail.updateRecordPrice(newEntity));
        } else if (request.assetsId() != null && request.assetsId() > 0) {
            // dto -> assets Entity
            AssetsDetail newAssetDetail = AssetsDetail.builder()
                    .user(user)
                    .assets(findAssets)
                    .priceType(request.priceType())
                    .category(findCategory)
                    .record(findRecord)
                    .type(request.type())
                    .price(request.type().equals(RecordType.DEPOSIT) ? request.price() : -request.price())
                    .active(BooleanYn.Y)
                    .useDate(LocalDate.parse(request.useDate()))
                    .memo(request.memo())
                    .balance(request.type().equals(RecordType.DEPOSIT) ?
                            findAssets.getBalance() + request.price() :
                            findAssets.getBalance() - request.price())
                    .build();
            // 등록
            assetsDetailRepository.save(newAssetDetail);
            // 자산 그룹 합계 금액 업데이트
            findAssets.updateBalance(newAssetDetail);
        }

        // 수정
        findRecord.update(newEntity);

        // 삭제할 이미지 있을 경우 데이터 삭제
        List<Long> deleteIds = request.deleteImages();
        if (request.deleteImages() != null && !request.deleteImages().isEmpty()) {
            List<RecordsImage> findRecordImages = recordsImageRepository.findByIdInAndStatus(deleteIds, StatusType.ACTIVE);
            for (RecordsImage findRecordImage : findRecordImages) {
                findRecordImage.remove();
            }
        }

        // 업로드할 이미지 있을 경우 이미지 업로드
        if (inputFiles != null && !inputFiles.isEmpty()) {
            List<MultipartFile> resizedFiles = new ArrayList<>();
            // 확장자 체크(png, jpg, jpeg)
            for (MultipartFile inputFile : inputFiles) {
                fileUtil.existsImageFileExtension(inputFile);
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
                        .user(user)
                        .records(findRecord)
                        .path(fileName.getModifiedFileName())
                        .build();
                recordsImages.add(recordsImage);
            }

            // 등록
            recordsImageRepository.saveAll(recordsImages);
        }
    }

    @Override
    public List<RecordsCurrentResponse> getCurrentRecord(LocalDate currentDate, User user) {
        // create condition
        RecordCurrentCondition condition = new RecordCurrentCondition(currentDate, user);
        return recordsRepository.findCurrentQuery(condition);
    }

    @Override
    public List<RecordsResponse> getMonthRecord(String date, User user) {
        // 해당 월의 첫 번째 날과 마지막 날 계산
        LocalDate startDate = LocalDate.parse(date.concat("-01"));
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // create condition
        RecordMonthCondition condition = new RecordMonthCondition(new Range(startDate, endDate), user);
        Map<LocalDate, List<RecordsMonthResponse>> groupedByDate = recordsRepository.findMonthQuery(condition).stream()
                .collect(Collectors.groupingBy(RecordsMonthResponse::getUserDate));

        // RecordsResponse 리스트로 변환
        return groupedByDate.entrySet().stream()
                .sorted(Map.Entry.<LocalDate, List<RecordsMonthResponse>>comparingByKey().reversed())
                .map(entry -> new RecordsResponse(
                        entry.getKey().toString(), // 또는 원하는 날짜 형식
                        entry.getValue()
                ))
                .toList();
    }

    @Override
    public RecordResponse getRecord(Long recordsId) {
        // 내역 조회
        Records findRecord = recordsRepository.findByIdAndStatus(recordsId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_RECORDS));
        // 이미지 조회
        List<RecordImagesResponse> images = recordsImageRepository.findAllByRecordsAndStatus(findRecord, StatusType.ACTIVE).stream()
                .map(RecordImagesResponse::new)
                .toList();

        return new RecordResponse(findRecord, images);
    }

    @Override
    @Transactional
    public void updateMemo(Long recordsId, RecordUpdateMemoRequest request) {
        // dto -> entity
        Records newEntity = request.toEntity();
        // 내역 조회
        Records findRecord = recordsRepository.findByIdAndStatus(recordsId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_RECORDS));
        // 메모 수정
        findRecord.update(newEntity);
    }

    @Override
    public RecordsCalendarResponse getCalendarRecord(String yearMonth, User user) {
        // 해당 월의 첫 번째 날과 마지막 날 계산
        LocalDate startDate = LocalDate.parse(yearMonth.concat("-01"));
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // create condition
        RecordCalendarCondition condition = new RecordCalendarCondition(new Range(startDate, endDate), user);

        // 내역 조회
        List<RecordsCalendarDetail> records = recordsRepository.findCalendarQuery(condition);
        int totalDeposit = records.stream().mapToInt(RecordsCalendarDetail::getDayDeposit).sum();
        int totalWithDraw = records.stream().mapToInt(RecordsCalendarDetail::getDayWithdraw).sum();
        return new RecordsCalendarResponse(totalDeposit, totalWithDraw, (totalWithDraw + totalDeposit), records);
    }
}
