package com.bank.gugu.domain.assetsDetail.repository;

import com.bank.gugu.domain.assetsDetail.repository.condition.AssetsCondition;
import com.bank.gugu.entity.assets.QAssets;
import com.bank.gugu.entity.assetsDetail.AssetsDetail;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.user.QUser;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.query.record.Range;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.bank.gugu.entity.assetsDetail.QAssetsDetail.*;

@Repository
@RequiredArgsConstructor
public class AssetsDetailRepositoryImpl implements AssetsDetailRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public List<AssetsDetail> findByQuery(Pageable pageable, AssetsCondition condition, User user) {
        return queryFactory.select(assetsDetail)
                .from(assetsDetail)
                .where(
                        notDeleteAssetsDetail(),
                        eqUser(user),
                        containKeyword(condition.keyword()),
                        eqType(condition.type()),
                        eqRange(condition.range()),
                        eqAssetsId(condition.assetsId())
                )
                .orderBy(checkSort(condition))
                .orderBy(checkCreatedSort(condition))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
    }

    /**
     * 일치하는 계좌 정보
     */
    private BooleanExpression eqAssetsId(Long assetsId) {
        return QAssets.assets.id.eq(assetsId);
    }

    /**
     * 등록순으로 정렬
     */
    private static OrderSpecifier<LocalDateTime> checkCreatedSort(AssetsCondition condition) {
        return condition.sort().equals("ASC") ? assetsDetail.createdAt.asc() : assetsDetail.createdAt.desc();
    }

    /**
     * 사용일로 정렬
     */
    private static OrderSpecifier<LocalDate> checkSort(AssetsCondition condition) {
        return condition.sort().equals("ASC") ? assetsDetail.useDate.asc() : assetsDetail.useDate.desc();
    }


    /**
     * 기간 검색
     */
    private BooleanExpression eqRange(Range range) {
        return assetsDetail.useDate.between(range.start(), range.end());
    }

    /**
     * 거래 유형 검색
     */
    private BooleanExpression eqType(RecordType type) {
        if(type.equals(RecordType.ALL)){
            return null;
        }
        return assetsDetail.type.eq(type);
    }

    /**
     * 키워드 검색
     */
    private BooleanExpression containKeyword(String keyword) {
        return assetsDetail.memo.containsIgnoreCase(keyword);
    }

    /**
     * 회원 정보 검색
     */
    private BooleanExpression eqUser(User user) {
        return QUser.user.eq(user);
    }

    /**
     * 삭제되지 않은 자산 상세내역 데이터
     */
    private BooleanExpression notDeleteAssetsDetail() {
        return assetsDetail.status.eq(StatusType.ACTIVE);
    }
}
