package com.bank.gugu.global.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.Set;

/**
 * QueryDsl 에서 사용하는 정렬 관련 클래스 입니다. <br>
 * ex) createdAt : createdAt 기준 asc 정렬 <br>
 *    -createdAt : createdAt 기준 desc 정렬
 * */
@Getter
public abstract class Sortable<T> {

    private final String property;
    private final boolean isDesc;
    private final Class<T> entityClass;
    private PathBuilder<T> pathBuilder;

    public Sortable(String sortBy, Class<T> entityClass) {
        if (ObjectUtils.isEmpty(sortBy)) {
            this.property = null;
            this.isDesc = false;
            this.entityClass = entityClass;
            return;
        }

        boolean isDescending = sortBy.startsWith("-");
        String fieldName = isDescending ? sortBy.substring(1) : sortBy;
        pathBuilder = new PathBuilder<>(entityClass, lowerFirstChar(entityClass.getSimpleName()));

        // 정렬 조건 검사
        if (!getAllowedSortFields().contains(fieldName)) {
            throw new IllegalArgumentException("올바르지 않은 정렬 조건입니다.");
        }

        this.property = fieldName;
        this.isDesc = isDescending;
        this.entityClass = entityClass;
    }


    public OrderSpecifier<?> toOrderSpecifier() {
        if (ObjectUtils.isEmpty(property)) {
            return OrderByNull.getDefault();
        }

        return isDesc
                ? pathBuilder.getComparable(property, Comparable.class).desc()
                : pathBuilder.getComparable(property, Comparable.class).asc();
    }

    /** 정렬을 허용하는 컬럼 */
    protected abstract Set<String> getAllowedSortFields();

    private String lowerFirstChar(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}