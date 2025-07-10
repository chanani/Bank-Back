package com.bank.gugu.domain.category.service;

import com.bank.gugu.domain.category.repository.CategoryRepository;
import com.bank.gugu.domain.category.service.dto.request.CategoryCreateRequest;
import com.bank.gugu.domain.category.service.dto.request.CategoryUpdateOrderRequest;
import com.bank.gugu.domain.category.service.dto.request.CategoryUpdateRequest;
import com.bank.gugu.domain.category.service.dto.response.CategoriesResponse;
import com.bank.gugu.domain.category.service.dto.response.CategoryResponse;
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
import java.util.Map;
import java.util.stream.Collectors;

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
        // 카테고리 순서 조회(제일 높은 번호 조회)
        Integer order = categoryRepository.findTopOrdersByUserAndStatus(user, StatusType.ACTIVE)
                .orElse(0);
        // dto -> entity
        Category newEntity = request.toEntity(user, findIcon, order);
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
        if (type.equals(RecordType.ALL)) {
            return categoryRepository.findByUserIdAndStatusOrderByOrdersAsc(user.getId(), StatusType.ACTIVE).stream()
                    .map(CategoriesResponse::new)
                    .toList();
        }
        return categoryRepository.findByUserIdAndTypeAndStatusOrderByOrdersAsc(user.getId(), type, StatusType.ACTIVE).stream()
                .map(CategoriesResponse::new)
                .toList();

    }

    @Override
    public CategoryResponse getCategory(Long categoryId) {
        Category findCategory = categoryRepository.findByIdAndStatus(categoryId, StatusType.ACTIVE)
                .orElseThrow(() -> new OperationErrorException(ErrorCode.NOT_FOUND_CATEGORY));
        return new CategoryResponse(findCategory);
    }

    @Override
    @Transactional
    public void updateOrder(List<CategoryUpdateOrderRequest> request, User user) {
        // 카테고리 목록 조회
        List<Category> categories = categoryRepository.findAllByUserIdAndStatus(user.getId(), StatusType.ACTIVE);

        // 요청받은 순서 업데이트 정보를 Map으로 변환 (빠른 검색을 위해)
        Map<Long, Integer> orderUpdateMap = request.stream()
                .collect(Collectors.toMap(
                        CategoryUpdateOrderRequest::id,
                        CategoryUpdateOrderRequest::order
                ));

        // 각 카테고리의 순서 업데이트
        categories.stream()
                .filter(category -> orderUpdateMap.containsKey(category.getId()))
                .forEach(category -> {
                    Integer newOrder = orderUpdateMap.get(category.getId());
                    category.updateOrder(newOrder);
                });
    }

    /**
     * 카테고리를 뒤쪽으로 이동 (순서 증가)
     */
    private void moveBackward(User user, Category targetCategory, Integer currentOrder, Integer requestOrder) {
        // 1. 이동할 범위의 카테고리들을 앞으로 한 칸씩 이동
        // currentOrder + 1부터 requestOrder까지의 카테고리들을 -1씩 이동
        List<Category> categoriesToShift = categoryRepository.findByUserAndOrdersBetween(
                user, currentOrder + 1, requestOrder);

        for (Category category : categoriesToShift) {
            category.updateOrder(category.getOrders() - 1);
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
        List<Category> categoriesToShift = categoryRepository.findByUserAndOrdersBetween(
                user, requestOrder, currentOrder - 1);

        for (Category category : categoriesToShift) {
            category.updateOrder(category.getOrders() + 1);
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
        Map<String, List<Icon>> icons = iconRepository.findAll().stream()
                .collect(Collectors.groupingBy(Icon::getName));

        return List.of(
                // 지출
                new Category(user, RecordType.WITHDRAW, "주거비", 0, icons.get("house").getFirst()),
                new Category(user, RecordType.WITHDRAW, "식비", 1, icons.get("food").getFirst()),
                new Category(user, RecordType.WITHDRAW, "교통", 2, icons.get("bus").getFirst()),
                new Category(user, RecordType.WITHDRAW, "공과금", 3, icons.get("paper").getFirst()),
                new Category(user, RecordType.WITHDRAW, "의료비", 4, icons.get("hospital").getFirst()),
                new Category(user, RecordType.WITHDRAW, "여행", 5, icons.get("airplain").getFirst()),
                new Category(user, RecordType.WITHDRAW, "생필품/마트", 6, icons.get("cart").getFirst()),
                new Category(user, RecordType.WITHDRAW, "선물/경조사", 7, icons.get("present").getFirst()),
                new Category(user, RecordType.WITHDRAW, "구독료", 8, icons.get("netflix").getFirst()),
                new Category(user, RecordType.WITHDRAW, "통신비", 9, icons.get("phone").getFirst()),
                new Category(user, RecordType.WITHDRAW, "운동", 10, icons.get("health2").getFirst()),
                new Category(user, RecordType.WITHDRAW, "교육", 11, icons.get("study").getFirst()),
                new Category(user, RecordType.WITHDRAW, "미용", 12, icons.get("hair").getFirst()),
                new Category(user, RecordType.WITHDRAW, "의류", 13, icons.get("shirt").getFirst()),

                // 수입
                new Category(user, RecordType.DEPOSIT, "월급", 14, icons.get("money").getFirst()),
                new Category(user, RecordType.DEPOSIT, "용돈", 15, icons.get("coin").getFirst())
        );
    }
}
