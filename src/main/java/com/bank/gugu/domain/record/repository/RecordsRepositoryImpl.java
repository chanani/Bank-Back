package com.bank.gugu.domain.record.repository;

import com.bank.gugu.domain.record.repository.condition.RecordCurrentCondition;
import com.bank.gugu.domain.record.repository.condition.RecordMonthCondition;
import com.bank.gugu.domain.record.service.dto.response.RecordsCurrentResponse;
import com.bank.gugu.domain.record.service.dto.response.RecordsMonthResponse;
import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.records.QRecords;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.query.record.Range;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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

    /**
     *
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
}
