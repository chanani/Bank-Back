package com.bank.gugu.controller.graph.input;

import com.bank.gugu.domain.graph.service.condition.GraphsCondition;
import com.bank.gugu.entity.common.GraphType;
import com.bank.gugu.entity.common.constant.RecordType;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.query.record.Range;
import io.swagger.v3.oas.annotations.media.Schema;

public record GraphsInput(
        @Schema(description = "월/년 타입", example = "MONTH")
        GraphType type,

        @Schema(description = "기간", example = "2025-06")
        String period,

        @Schema(description = "입/출금 타입")
        RecordType recordType
) {
    public GraphsCondition toCondition(Range range, User user) {
        return new GraphsCondition(range, this.recordType, user);
    }
}
