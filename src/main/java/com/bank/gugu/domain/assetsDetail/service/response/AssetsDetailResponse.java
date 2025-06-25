package com.bank.gugu.domain.assetsDetail.service.response;

import com.bank.gugu.entity.assetsDetail.AssetsDetail;
import com.bank.gugu.entity.common.constant.RecordType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AssetsDetailResponse {

    @Schema(description = "자산 상세정보 ID")
    private Long id;

    @Schema(description = "타입")
    private RecordType type;

    @Schema(description = "금액")
    private Integer price;

    @Schema(description = "변경 후 잔액")
    private Integer balance;

    @Schema(description = "날짜")
    private LocalDate useDate;

    @Schema(description = "메모")
    private String memo;

    public AssetsDetailResponse(AssetsDetail assetsDetail) {
        this.id = assetsDetail.getId();
        this.type = assetsDetail.getType();
        this.price = assetsDetail.getPrice();
        this.balance = assetsDetail.getBalance();
        this.useDate = assetsDetail.getUseDate();
        this.memo = assetsDetail.getMemo();
    }
}
