package com.bank.gugu.domain.assetsDetail.service.response;

import com.bank.gugu.entity.assets.Assets;
import com.bank.gugu.entity.assetsDetail.AssetsDetail;
import com.bank.gugu.entity.common.constant.PriceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;

@Getter
public class AssetsDetailsTotalResponse {

    @Schema(description = "자산 상세정보 ID")
    private Long id;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "색상")
    private String color;

    @Schema(description = "금액")
    private Integer balance;

    @Schema(description = "계좌 내역 상세 목록")
    private Slice<AssetsDetailsResponse> assetsDetails;

    public AssetsDetailsTotalResponse(Assets assets, Slice<AssetsDetailsResponse> assetsDetails) {
        this.id = assets.getId();
        this.name = assets.getName();
        this.balance = assets.getBalance();
        this.color = assets.getColor();
        this.assetsDetails = assetsDetails;
    }
}
