package com.bank.gugu.domain.record.service.dto.response;

import com.bank.gugu.entity.common.constant.PriceType;
import com.bank.gugu.entity.common.constant.RecordType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RecordsMonthResponse {

    @Schema(description = "입/출금 내역 ID")
    private Long id;

    @Schema(description = "카테고리 아이콘")
    private String iconPath;

    @Schema(description = "카테고리명")
    private String categoryName;

    @Schema(description = "타입")
    private RecordType type;

    @Schema(description = "금액")
    private Integer price;

    @Schema(description = "결제 유형")
    private String priceType;

    @Schema(description = "할부")
    private Integer monthly;

    @Schema(description = "메모")
    private String memo;

    @Schema(description = "날짜")
    private LocalDate userDate;

    @Schema(description = "이미지 존재 여부")
    private boolean imageStatus;

    public RecordsMonthResponse(Long id, String iconPath, String categoryName, RecordType type,
                                Integer price, PriceType priceType,
                                Integer monthly, String memo, LocalDate userDate, boolean imageStatus) {
        this.id = id;
        this.iconPath = iconPath;
        this.categoryName = categoryName;
        this.type = type;
        this.price = price;
        this.priceType = priceType.getValue();
        this.monthly = monthly;
        this.memo = memo;
        this.userDate = userDate;
        this.imageStatus = imageStatus;
    }
}
