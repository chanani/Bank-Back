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
import com.bank.gugu.global.utils.FileUtil;
import com.bank.gugu.global.utils.dto.FileName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final AssetsRepository assetsRepository;
    private final CategoryRepository categoryRepository;
    private final AssetsDetailRepository assetsDetailRepository;
    private final FileUtil fileUtil;
    private final RecordsImageRepository recordsImageRepository;

    @Override
    @Transactional
    public void addRecord(RecordCreateRequest request, User user, List<MultipartFile> inputFiles) throws IOException {
        Category findCategory = findCategory(request.categoryId());

        Assets findAssets = findAssets(request.assetsId());
        Records newEntity = request.toEntity(user, findCategory, findAssets);
        Records saveRecord = recordsRepository.save(newEntity);

        if (isBlackAssets(request)) {
            AssetsDetail newAssetsDetailEntity = request.toAssetsDetail(user, findAssets, saveRecord, findCategory);
            assetsDetailRepository.save(newAssetsDetailEntity);
            findAssets.updateBalance(newAssetsDetailEntity);
        }

        if (isBlankFile(inputFiles)) {
            List<MultipartFile> resizedFiles = resizingFile(inputFiles);
            List<RecordsImage> recordsImages = uploadImageFile(user, resizedFiles, saveRecord);
            recordsImageRepository.saveAll(recordsImages);
        }
    }

    @Override
    @Transactional
    public void deleteRecord(Long recordsId) {
        Records findRecord = findRecord(recordsId);

        findRecord.remove();

        removeAssetsDetail(findRecord);
    }

    @Override
    @Transactional
    public void updateRecord(RecordUpdateRequest request, Long recordsId, List<MultipartFile> inputFiles, User user) throws IOException {
        Category findCategory = findCategory(request.categoryId());

        Assets findAssets = findAssets(request.assetsId());

        Records newEntity = request.toEntity(findCategory, findAssets);

        Records findRecord = findRecord(recordsId);

        if (findRecord.getAssets() != null) {
            modifyAssets(findRecord, newEntity);
        } else if (request.assetsId() != null && request.assetsId() > 0) {
            AssetsDetail newAssetDetail = createAssetsDetail(request, user, findAssets, findCategory, findRecord);
            assetsDetailRepository.save(newAssetDetail);
            findAssets.updateBalance(newAssetDetail);
        }

        findRecord.update(newEntity);

        removeImageFile(request);

        if (isBlankFile(inputFiles)) {
            List<MultipartFile> resizedFiles = resizingFile(inputFiles);
            List<RecordsImage> recordsImages = uploadImageFile(user, resizedFiles, findRecord);
            recordsImageRepository.saveAll(recordsImages);
        }
    }


    @Override
    public List<RecordsCurrentResponse> getCurrentRecord(LocalDate currentDate, User user) {
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
                        entry.getKey().toString(),
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
    @Transactional(readOnly = true)
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

    private Category findCategory(Long request) {
        return categoryRepository.findByIdAndStatus(request, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_CATEGORY));
    }

    private Assets findAssets(Long request) {
        if (isBlank(request)) {
            return assetsRepository.findByIdAndStatus(request, StatusType.ACTIVE)
                    .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ASSETS));
        }
        return null;
    }

    private Records findRecord(Long recordsId) {
        return recordsRepository.findByIdAndStatus(recordsId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_RECORDS));
    }

    private static boolean isBlank(Long request) {
        return request != null && request > 0;
    }

    private static boolean isBlackAssets(RecordCreateRequest request) {
        return request.assetsId() != null && request.assetsId() > 0;
    }

    private static boolean isBlankFile(List<MultipartFile> inputFiles) {
        return inputFiles != null && !inputFiles.isEmpty();
    }

    private List<MultipartFile> resizingFile(List<MultipartFile> inputFiles) throws IOException {
        List<MultipartFile> resizedFiles = new ArrayList<>();
        for (MultipartFile inputFile : inputFiles) {
            fileUtil.existsImageFileExtension(inputFile);
            MultipartFile resizedFile = fileUtil.resizeImage(inputFile);
            resizedFiles.add(resizedFile);
        }
        return resizedFiles;
    }

    private List<RecordsImage> uploadImageFile(User user, List<MultipartFile> resizedFiles, Records saveRecord) {
        List<FileName> fileNames = fileUtil.filesUpload(resizedFiles, "fileImage");

        List<RecordsImage> recordsImages = new ArrayList<>();
        for (FileName fileName : fileNames) {
            RecordsImage recordsImage = addRecordImage(user, saveRecord, fileName);
            recordsImages.add(recordsImage);
        }
        return recordsImages;
    }

    private static RecordsImage addRecordImage(User user, Records saveRecord, FileName fileName) {
        return RecordsImage.builder()
                .user(user)
                .records(saveRecord)
                .path(fileName.getModifiedFileName())
                .build();
    }

    private void removeImageFile(RecordUpdateRequest request) {
        List<Long> deleteIds = request.deleteImages();
        if (isBlankDeleteImageFile(deleteIds)) {
            List<RecordsImage> findRecordImages = recordsImageRepository.findByIdInAndStatus(deleteIds, StatusType.ACTIVE);
            for (RecordsImage findRecordImage : findRecordImages) {
                findRecordImage.remove();
            }
        }
    }

    private static boolean isBlankDeleteImageFile(List<Long> deleteIds) {
        return deleteIds != null && !deleteIds.isEmpty();
    }

    private void modifyAssets(Records findRecord, Records newEntity) {
        assetsRepository.findByIdAndStatus(findRecord.getAssets().getId(), StatusType.ACTIVE)
                .ifPresent(assets -> assets.updateRecordBalance(findRecord, newEntity));
        assetsDetailRepository.findByRecordIdAndStatus(findRecord.getId(), StatusType.ACTIVE)
                .ifPresent(assetsDetail -> assetsDetail.updateRecordPrice(newEntity));
    }

    private static AssetsDetail createAssetsDetail(RecordUpdateRequest request, User user, Assets findAssets, Category findCategory, Records findRecord) {
        return AssetsDetail.builder()
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
    }

    private void removeAssetsDetail(Records findRecord) {
        if (findRecord.getAssets() != null) {
            assetsRepository.findByIdAndStatus(findRecord.getAssets().getId(), StatusType.ACTIVE)
                    .ifPresent(findAssets -> findAssets.removeBalance(findRecord));
            assetsDetailRepository.findByRecordIdAndStatus(findRecord.getId(), StatusType.ACTIVE)
                    .ifPresent(BaseEntity::remove);
        }
    }
}
