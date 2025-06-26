package com.bank.gugu.domain.category.service;

import com.bank.gugu.domain.category.repository.CategoryRepository;
import com.bank.gugu.domain.category.service.dto.request.CategoryCreateRequest;
import com.bank.gugu.domain.category.service.dto.request.CategoryUpdateOrderRequest;
import com.bank.gugu.domain.category.service.dto.request.CategoryUpdateRequest;
import com.bank.gugu.domain.category.service.dto.response.CategoriesResponse;
import com.bank.gugu.domain.icon.repository.IconRepository;
import com.bank.gugu.entity.category.Category;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.icon.Icon;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.exception.OperationErrorException;
import com.bank.gugu.global.exception.dto.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DefaultCategoryService implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final IconRepository iconRepository;

    @Override
    @Transactional
    public void addCategories(User user) {
        List<Category> categories = defaultCategories(user);
        categoryRepository.saveAll(categories);
    }

    @Override
    @Transactional
    public void addCategory(CategoryCreateRequest request, User user) {
        // 아이콘 전달 했을 경우 아이콘 조회
        Icon findIcon = null;
        if (request.icon() != 0 && request.icon() != null) {
            findIcon = iconRepository.findByIdAndStatus(request.icon(), StatusType.ACTIVE)
                    .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ICON));
        }
        // dto -> entity
        Category newEntity = request.toEntity(user, findIcon);
        categoryRepository.save(newEntity);
    }

    @Override
    @Transactional
    public void updateCategory(Long categoryId, CategoryUpdateRequest request, User user) {
        // 아이콘 전달 했을 경우 아이콘 조회
        Icon findIcon = null;
        if (request.icon() != 0 && request.icon() != null) {
            findIcon = iconRepository.findByIdAndStatus(request.icon(), StatusType.ACTIVE)
                    .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_ICON));
        }
        // 카테고리 조회
        Category findCategory = categoryRepository.findByIdAndStatus(categoryId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_CATEGORY));
        // dto -> entity
        Category newEntity = request.toEntity(user, findIcon);
        // 수정 진행
        findCategory.update(newEntity);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        // 카테고리 조회
        Category findCategory = categoryRepository.findByIdAndStatus(categoryId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_CATEGORY));
        // 카테고리 소프트 삭제
        findCategory.remove();
    }

    @Override
    public List<CategoriesResponse> getCategories(User user, RecordType type) {
        return categoryRepository.findByUserIdAndTypeAndStatus(user.getId(), type, StatusType.ACTIVE).stream()
                .map(CategoriesResponse::new)
                .toList();

    }

    @Override
    @Transactional
    public void updateOrder(CategoryUpdateOrderRequest request, User user) {
        Integer currentOrder = request.currentOrder();
        Integer requestOrder = request.requestOrder();

        // 현재 순서의 카테고리 조회
        Category targetCategory = categoryRepository.findByUserAndOrder(user, currentOrder)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_CATEGORY));

        // 요청 순서가 현재 순서와 같으면 변경할 필요 없음
        if (currentOrder.equals(requestOrder)) {
            return;
        }

        // 순서 변경 로직 실행
        if (currentOrder < requestOrder) {
            // 현재 순서가 요청 순서보다 작을 때 (뒤로 이동)
            moveBackward(user, targetCategory, currentOrder, requestOrder);
        } else {
            // 현재 순서가 요청 순서보다 클 때 (앞으로 이동)
            moveForward(user, targetCategory, currentOrder, requestOrder);
        }
    }

    /**
     * 카테고리를 뒤쪽으로 이동 (순서 증가)
     */
    private void moveBackward(User user, Category targetCategory, Integer currentOrder, Integer requestOrder) {
        // 1. 이동할 범위의 카테고리들을 앞으로 한 칸씩 이동
        // currentOrder + 1부터 requestOrder까지의 카테고리들을 -1씩 이동
        List<Category> categoriesToShift = categoryRepository.findByUserAndOrderBetween(
                user, currentOrder + 1, requestOrder);

        for (Category category : categoriesToShift) {
            category.updateOrder(category.getOrder() - 1);
        }

        // 2. 대상 카테고리를 새 위치로 이동
        targetCategory.updateOrder(requestOrder);

        // 3. 변경사항 저장
        categoryRepository.saveAll(categoriesToShift);
        categoryRepository.save(targetCategory);
    }

    /**
     * 카테고리를 앞쪽으로 이동 (순서 감소)
     */
    private void moveForward(User user, Category targetCategory, Integer currentOrder, Integer requestOrder) {
        // 1. 이동할 범위의 카테고리들을 뒤로 한 칸씩 이동
        // requestOrder부터 currentOrder - 1까지의 카테고리들을 +1씩 이동
        List<Category> categoriesToShift = categoryRepository.findByUserAndOrderBetween(
                user, requestOrder, currentOrder - 1);

        for (Category category : categoriesToShift) {
            category.updateOrder(category.getOrder() + 1);
        }

        // 2. 대상 카테고리를 새 위치로 이동
        targetCategory.updateOrder(requestOrder);

        // 3. 변경사항 저장
        categoryRepository.saveAll(categoriesToShift);
        categoryRepository.save(targetCategory);
    }

    /**
     * 기본 카테고리 목록
     */
    public List<Category> defaultCategories(User user) {
        List<Icon> icons = iconRepository.findAll();

        return List.of(
                // 지출
                new Category(user, RecordType.WITHDRAW, "주거비", 0, icons.get(0)),
                new Category(user, RecordType.WITHDRAW, "식비", 1, icons.get(1)),
                new Category(user, RecordType.WITHDRAW, "교통", 2, icons.get(2)),
                new Category(user, RecordType.WITHDRAW, "공과금", 3, icons.get(3)),
                new Category(user, RecordType.WITHDRAW, "의료비", 4, icons.get(4)),
                new Category(user, RecordType.WITHDRAW, "문화생활", 5, icons.get(5)),
                new Category(user, RecordType.WITHDRAW, "생필품/마트", 6, icons.get(6)),
                new Category(user, RecordType.WITHDRAW, "선물/경조사", 7, icons.get(7)),
                new Category(user, RecordType.WITHDRAW, "구독료", 8, icons.get(8)),
                new Category(user, RecordType.WITHDRAW, "통신비", 9, icons.get(9)),
                new Category(user, RecordType.WITHDRAW, "운동", 10, icons.get(10)),
                new Category(user, RecordType.WITHDRAW, "교육", 11, icons.get(11)),
                new Category(user, RecordType.WITHDRAW, "미용", 12, icons.get(12)),
                new Category(user, RecordType.WITHDRAW, "의류", 13, icons.get(13)),
                new Category(user, RecordType.WITHDRAW, "여행", 14, icons.get(14)),
                new Category(user, RecordType.WITHDRAW, "육아", 15, icons.get(15)),

                // 수입
                new Category(user, RecordType.DEPOSIT, "월급", 16, icons.get(16)),
                new Category(user, RecordType.DEPOSIT, "용돈", 17, icons.get(17)),
                new Category(user, RecordType.DEPOSIT, "보너스/상여금", 18, icons.get(18)),
                new Category(user, RecordType.DEPOSIT, "부수입", 19, icons.get(19))
        );
    }
}
