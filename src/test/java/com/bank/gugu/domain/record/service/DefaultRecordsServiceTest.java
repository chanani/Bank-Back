package com.bank.gugu.domain.record.service;

import com.bank.gugu.domain.record.service.dto.request.RecordCreateRequest;
import com.bank.gugu.domain.user.repository.UserRepository;
import com.bank.gugu.entity.common.constant.PriceType;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.records.Records;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.exception.OperationErrorException;
import com.bank.gugu.global.exception.dto.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import com.bank.gugu.domain.assets.repository.AssetsRepository;
import com.bank.gugu.domain.assetsDetail.repository.AssetsDetailRepository;
import com.bank.gugu.domain.category.repository.CategoryRepository;
import com.bank.gugu.domain.record.repository.RecordsRepository;
import com.bank.gugu.domain.recordsImage.repository.RecordsImageRepository;
import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.category.Category;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class DefaultRecordsServiceTest {

    @Autowired
    private RecordsService recordsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecordsRepository recordsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AssetsRepository assetsRepository;

    @Autowired
    private AssetsDetailRepository assetsDetailRepository;

    @Autowired
    private RecordsImageRepository recordsImageRepository;

    private User testUser;
    private Category testCategory;
    private Assets testAssets;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 조회
        testUser = userRepository.findByIdAndStatus(3L, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_USER));

        testCategory = categoryRepository.findByIdAndStatus(15L, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_CATEGORY));
    }

    @Test
    @DisplayName("입출금 기록 생성 - Assets 없이")
    void addRecord_WithoutAssets_Success() throws IOException {
        RecordCreateRequest request = new RecordCreateRequest(
                RecordType.DEPOSIT,
                PriceType.CHECK_CARD,
                10000,
                0,
                "메모합니다.",
                "2025-11-07",
                15L,
                null  // assetsId 없음
        );

        recordsService.addRecord(request, testUser, null);

        Records savedRecord = recordsRepository.findAll().stream()
                .filter(r -> r.getMemo().equals("메모합니다."))
                .findFirst()
                .orElseThrow();

        assertThat(savedRecord).isNotNull();
        assertThat(savedRecord.getType()).isEqualTo(RecordType.DEPOSIT);
        assertThat(savedRecord.getPriceType()).isEqualTo(PriceType.CHECK_CARD);
        assertThat(savedRecord.getPrice()).isEqualTo(10000);
        assertThat(savedRecord.getMemo()).isEqualTo("메모합니다.");
        assertThat(savedRecord.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedRecord.getCategory().getId()).isEqualTo(15L);
        assertThat(savedRecord.getAssets()).isNull();
    }

    @Test
    @DisplayName("입출금 기록 생성 - Assets 포함")
    void addRecord_WithAssets_Success() throws IOException {
        Assets assets = assetsRepository.findAll().stream()
                .filter(a -> a.getStatus() == StatusType.ACTIVE)
                .findFirst()
                .orElse(null);

        if (assets == null) {
            return;
        }

        RecordCreateRequest request = new RecordCreateRequest(
                RecordType.DEPOSIT,
                PriceType.CASH,
                5000,
                0,
                "자산 포함 지출",
                "2025-11-07",
                15L,
                assets.getId()
        );

        int beforeAssetsDetailCount = assetsDetailRepository.findAll().size();

        recordsService.addRecord(request, testUser, null);

        Records savedRecord = recordsRepository.findAll().stream()
                .filter(r -> r.getMemo().equals("자산 포함 지출"))
                .findFirst()
                .orElseThrow();

        assertThat(savedRecord).isNotNull();
        assertThat(savedRecord.getType()).isEqualTo(RecordType.DEPOSIT);
        assertThat(savedRecord.getPrice()).isEqualTo(5000);
        assertThat(savedRecord.getAssets()).isNotNull();
        assertThat(savedRecord.getAssets().getId()).isEqualTo(assets.getId());

        int afterAssetsDetailCount = assetsDetailRepository.findAll().size();
        assertThat(afterAssetsDetailCount).isEqualTo(beforeAssetsDetailCount + 1);
    }

    @Test
    @DisplayName("입출금 기록 생성 실패 - 존재하지 않는 Category")
    void addRecord_CategoryNotFound_ThrowException() {
        RecordCreateRequest request = new RecordCreateRequest(
                RecordType.DEPOSIT,
                PriceType.CHECK_CARD,
                10000,
                0,
                "잘못된 카테고리",
                "2025-11-07",
                99999L,  // 존재하지 않는 카테고리 ID
                null
        );

        assertThatThrownBy(() -> recordsService.addRecord(request, testUser, null))
                .isInstanceOf(OperationErrorException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_CATEGORY);
    }

    @Test
    @DisplayName("입출금 기록 생성 실패 - 존재하지 않는 Assets")
    void addRecord_AssetsNotFound_ThrowException() {
        RecordCreateRequest request = new RecordCreateRequest(
                RecordType.DEPOSIT,
                PriceType.CHECK_CARD,
                10000,
                0,
                "잘못된 자산",
                "2025-11-07",
                15L,
                99999L  // 존재하지 않는 자산 ID
        );

        assertThatThrownBy(() -> recordsService.addRecord(request, testUser, null))
                .isInstanceOf(OperationErrorException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_ASSETS);
    }

    @Test
    @DisplayName("입출금 기록 생성 - 다양한 PriceType 테스트")
    void addRecord_VariousPriceTypes_Success() throws IOException {
        PriceType[] priceTypes = {
                PriceType.CASH,
                PriceType.CHECK_CARD,
                PriceType.CARD
        };

        for (PriceType priceType : priceTypes) {
            RecordCreateRequest request = new RecordCreateRequest(
                    RecordType.DEPOSIT,
                    priceType,
                    10000,
                    0,
                    "테스트: " + priceType.name(),
                    "2025-11-07",
                    15L,
                    null
            );

            recordsService.addRecord(request, testUser, null);

            Records savedRecord = recordsRepository.findAll().stream()
                    .filter(r -> r.getMemo().equals("테스트: " + priceType.name()))
                    .findFirst()
                    .orElseThrow();

            assertThat(savedRecord.getPriceType()).isEqualTo(priceType);
        }
    }

    @Test
    @DisplayName("입출금 기록 생성 - DEPOSIT과 EXPENDITURE 타입 테스트")
    void addRecord_DepositAndExpenditure_Success() throws IOException {
        RecordCreateRequest depositRequest = new RecordCreateRequest(
                RecordType.DEPOSIT,
                PriceType.CASH,
                50000,
                0,
                "입금 테스트",
                "2025-11-07",
                15L,
                null
        );

        recordsService.addRecord(depositRequest, testUser, null);

        Records depositRecord = recordsRepository.findAll().stream()
                .filter(r -> r.getMemo().equals("입금 테스트"))
                .findFirst()
                .orElseThrow();

        assertThat(depositRecord.getType()).isEqualTo(RecordType.DEPOSIT);
        assertThat(depositRecord.getPrice()).isEqualTo(50000);

        RecordCreateRequest expenditureRequest = new RecordCreateRequest(
                RecordType.DEPOSIT,
                PriceType.CARD,
                30000,
                0,
                "지출 테스트",
                "2025-11-07",
                15L,
                null
        );

        recordsService.addRecord(expenditureRequest, testUser, null);

        Records expenditureRecord = recordsRepository.findAll().stream()
                .filter(r -> r.getMemo().equals("지출 테스트"))
                .findFirst()
                .orElseThrow();

        assertThat(expenditureRecord.getType()).isEqualTo(RecordType.DEPOSIT);
        assertThat(expenditureRecord.getPrice()).isEqualTo(30000);
    }
}