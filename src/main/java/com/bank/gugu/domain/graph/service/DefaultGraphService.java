package com.bank.gugu.domain.graph.service;

import com.bank.gugu.controller.graph.input.GraphsInput;
import com.bank.gugu.domain.graph.service.condition.GraphsCondition;
import com.bank.gugu.domain.graph.service.dto.response.GraphCategories;
import com.bank.gugu.domain.graph.service.dto.response.GraphsResponse;
import com.bank.gugu.domain.record.repository.RecordsRepository;
import com.bank.gugu.entity.common.GraphType;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.query.record.Range;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DefaultGraphService implements GraphService {

    private final RecordsRepository recordsRepository;

    @Override
    public GraphsResponse getGraphs(GraphsInput input, User user) {
        // 날짜 범위 선정
        Range range = createRange(input);
        // input -> condition
        GraphsCondition condition = input.toCondition(range, user);
        // 데이터 조회 및 percent 기준으로 내림차순 정렬
        List<GraphCategories> graphs = recordsRepository.findGraphQuery(condition)
                .stream()
                .sorted(Comparator.comparing(GraphCategories::getPercent).reversed())
                .toList();

        Integer balance = graphs.stream().mapToInt(GraphCategories::getSumPrice).sum();
        return new GraphsResponse(balance, graphs);
    }

    private Range createRange(GraphsInput input) {
        GraphType type = input.type();
        String period = input.period();

        if (type == null || period == null) {
            return Range.defaultRange();
        }

        try {
            switch (type) {
                case MONTH:
                    return createMonthRange(period);
                case YEAR:
                    return createYearRange(period);
                default:
                    throw new IllegalArgumentException("지원하지 않는 타입입니다: " + type);
            }
        } catch (Exception e) {
            // 파싱 오류 시 예외 던지기
            throw new IllegalArgumentException("잘못된 기간 형식입니다: " + period, e);
        }
    }

    /**
     * 월 범위 생성 (yyyy-MM 형식)
     * 예: "2025-06" → 2025-06-01 ~ 2025-06-30
     */
    private Range createMonthRange(String period) {
        try {
            // yyyy-MM 형식 파싱
            YearMonth yearMonth = YearMonth.parse(period, DateTimeFormatter.ofPattern("yyyy-MM"));

            LocalDate startDate = yearMonth.atDay(1); // 해당 월의 첫째 날
            LocalDate endDate = yearMonth.atEndOfMonth(); // 해당 월의 마지막 날

            return Range.create(startDate, endDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("월 형식이 올바르지 않습니다. yyyy-MM 형식을 사용해주세요: " + period, e);
        }
    }

    /**
     * 연도 범위 생성 (yyyy 형식)
     * 예: "2025" → 2025-01-01 ~ 2025-12-31
     */
    private Range createYearRange(String period) {
        try {
            // yyyy 형식 파싱
            int year = Integer.parseInt(period);

            // 연도 유효성 검증 (1900-2100 범위)
            if (year < 1900 || year > 2100) {
                throw new IllegalArgumentException("연도는 1900-2100 범위여야 합니다: " + year);
            }

            LocalDate startDate = LocalDate.of(year, 1, 1); // 해당 연도의 첫째 날
            LocalDate endDate = LocalDate.of(year, 12, 31); // 해당 연도의 마지막 날

            return Range.create(startDate, endDate);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("연도 형식이 올바르지 않습니다. yyyy 형식을 사용해주세요: " + period, e);
        }
    }


}
