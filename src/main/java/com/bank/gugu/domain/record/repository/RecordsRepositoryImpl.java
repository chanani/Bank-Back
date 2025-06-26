package com.bank.gugu.domain.record.repository;

import com.bank.gugu.domain.graph.service.condition.GraphsCondition;
import com.bank.gugu.domain.graph.service.dto.response.GraphCategories;
import com.bank.gugu.domain.graph.service.dto.response.GraphCategorySummary;
import com.bank.gugu.domain.record.repository.condition.RecordCalendarCondition;
import com.bank.gugu.domain.record.repository.condition.RecordCurrentCondition;
import com.bank.gugu.domain.record.repository.condition.RecordMonthCondition;
import com.bank.gugu.domain.record.service.dto.response.RecordsCalendarDetail;
import com.bank.gugu.domain.record.service.dto.response.RecordsCurrentResponse;
import com.bank.gugu.domain.record.service.dto.response.RecordsMonthResponse;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.query.record.Range;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.bank.gugu.entity.category.QCategory.*;
import static com.bank.gugu.entity.records.QRecords.*;

@Repository
@RequiredArgsConstructor
public class RecordsRepositoryImpl implements RecordsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RecordsCurrentResponse> findCurrentQuery(RecordCurrentCondition condition) {
        return queryFactory.select(Projections.constructor(
                        RecordsCurrentResponse.class,
                        records.id,
                        records.category.icon.path,
                        records.type,
                        records.price,
                        records.priceType,
                        records.monthly,
                        records.memo,
                        records.useDate
                ))
                .from(records)
                .where(
                        notDeleteRecord(),
                        eqUser(condition.user()),
                        eqUseDate(condition.currentDate())
                )
                .orderBy(records.createdAt.desc())
                .fetch();
    }

    @Override
    public List<RecordsMonthResponse> findMonthQuery(RecordMonthCondition condition) {
        return queryFactory.select(Projections.constructor(
                        RecordsMonthResponse.class,
                        records.id,
                        records.category.icon.path,
                        records.type,
                        records.price,
                        records.priceType,
                        records.monthly,
                        records.memo,
                        records.useDate
                ))
                .from(records)
                .where(
                        notDeleteRecord(),
                        eqUser(condition.user()),
                        eqRange(condition.range())
                )
                .orderBy(records.createdAt.desc())
                .fetch();
    }

    @Override
    public List<GraphCategories> findGraphQuery(GraphsCondition condition) {
        // 1단계: 전체 레코드의 price 합계를 조회합니다.
        // 이 쿼리는 기존에 사용하던 JPAQueryFactory로 실행됩니다.
        var totalSumOfAllRecords = queryFactory
                .select(records.price.sum())
                .from(records)
                .where(
                        notDeleteRecord(),
                        eqUser(condition.user()),
                        eqRange(condition.range()),
                        eqType(condition.type())
                )
                .fetchOne(); // 단일 결과 (전체 합계)를 가져옵니다.

        // totalSumOfAllRecords가 null이거나 0일 경우를 대비합니다.
        if (totalSumOfAllRecords == null || totalSumOfAllRecords == 0.0) {
            return new ArrayList<>(); // 계산할 수 없으므로 빈 리스트 반환
        }

        // 2단계: 각 카테고리별 건수와 price 합계를 조회합니다.
        // 이 쿼리도 JPAQueryFactory로 실행됩니다.
        List<GraphCategorySummary> summaries = queryFactory
                .select(Projections.constructor(
                        GraphCategorySummary.class,
                        category.id,
                        category.name,
                        category.icon.path,
                        records.count(),
                        records.price.sum()
                ))
                .from(records)
                .join(category)
                .on(records.category.eq(category))
                .where(
                        notDeleteRecord(),
                        eqUser(condition.user()),
                        eqRange(condition.range()),
                        eqType(condition.type()),
                        notDeleteCategory()
                )
                .groupBy(category.id, category.name, category.icon.path)
                .fetch();

        // 3단계: Java 코드에서 퍼센트를 계산하고 GraphCategories DTO로 변환합니다.
        List<GraphCategories> finalResults = new ArrayList<>();
        for (GraphCategorySummary summary : summaries) {
            double percentValue = (summary.getSumPriceRaw() * 100.0 / totalSumOfAllRecords);
            // 소수점 둘째 자리까지 반올림 (SQL의 ROUND(..., 2)와 동일하게)
            percentValue = Math.round(percentValue * 100.0) / 100.0;

            finalResults.add(new GraphCategories(
                    summary.getCategoryId(),
                    summary.getCategoryName(),
                    summary.getIconPath(),
                    summary.getSumPriceRaw(),
                    summary.getUseCountRaw().intValue(),
                    percentValue
            ));
        }

        return finalResults;
    }

    @Override
    public List<RecordsCalendarDetail> findCalendarQuery(RecordCalendarCondition condition) {
        // use_date에서 '일(day)'만 추출하는 Expression
        // SQL 함수 호출 템플릿 사용
        // 주의: 사용하는 DB에 맞는 'FUNCTION' 이름으로 변경해야 합니다.
        // MySQL: "FUNCTION('DAYOFMONTH', {0})"
        // PostgreSQL/H2: "FUNCTION('EXTRACT', {0}, 'DAY')" 또는 "FUNCTION('DAY', {0})" (DB에 따라)
        Expression<Integer> dayExpression = Expressions.numberTemplate(
                Integer.class,
                "FUNCTION('DAY', {0})", // 이 부분이 사용하시는 DB의 '일' 추출 함수여야 합니다.
                // 예를 들어 MySQL에서는 "FUNCTION('DAYOFMONTH', {0})"
                records.useDate
        );

        // DEPOSIT 타입의 price 합계 (Integer로 반환)
        NumberExpression<Integer> depositSum = new CaseBuilder()
                .when(records.type.eq(RecordType.DEPOSIT))
                .then(records.price)
                .otherwise(0)
                .sum()
                .castToNum(Integer.class)
                .as("dayDeposit");

        // WITHDRAW 타입의 price 합계 (Integer로 반환)
        NumberExpression<Integer> withdrawSum = new CaseBuilder()
                .when(records.type.eq(RecordType.WITHDRAW))
                .then(records.price)
                .otherwise(0)
                .sum()
                .castToNum(Integer.class)
                .as("dayWithdraw");


        return queryFactory
                .select(Projections.constructor(
                        RecordsCalendarDetail.class,
                        dayExpression,
                        depositSum,
                        withdrawSum
                ))
                .from(records)
                .where(
                        notDeleteRecord(),
                        eqUser(condition.user()),
                        eqRange(condition.range())
                )
                .groupBy(dayExpression)
                .orderBy(new OrderSpecifier<>(Order.ASC, dayExpression))
                .fetch();
    }

    /**
     * 타입 검색
     */
    private BooleanExpression eqType(RecordType type) {
        return records.type.eq(type);
    }

    /**
     * 날짜 범위 검색
     */
    private BooleanExpression eqRange(Range range) {
        return records.useDate.between(range.start(), range.end());
    }

    /**
     * 날짜 검색
     */
    private BooleanExpression eqUseDate(LocalDate currentDate) {
        return records.useDate.eq(currentDate);
    }

    /**
     * 회원 검색
     */
    private BooleanExpression eqUser(User user) {
        return records.user.eq(user);
    }

    /**
     * 삭제되지 않은 데이터
     */
    private BooleanExpression notDeleteRecord() {
        return records.status.eq(StatusType.ACTIVE);
    }

    /**
     * 삭제되지 않은 카테고리 데이터
     */
    private BooleanExpression notDeleteCategory() {
        return category.status.eq(StatusType.ACTIVE);
    }
}
