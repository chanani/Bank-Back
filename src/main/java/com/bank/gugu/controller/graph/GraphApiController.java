package com.bank.gugu.controller.graph;

import com.bank.gugu.controller.graph.input.GraphsInput;
import com.bank.gugu.domain.graph.service.GraphService;
import com.bank.gugu.domain.graph.service.dto.response.GraphsResponse;
import com.bank.gugu.domain.record.service.RecordsService;
import com.bank.gugu.domain.record.service.dto.request.RecordCreateRequest;
import com.bank.gugu.domain.record.service.dto.request.RecordUpdateMemoRequest;
import com.bank.gugu.domain.record.service.dto.request.RecordUpdateRequest;
import com.bank.gugu.domain.record.service.dto.response.RecordResponse;
import com.bank.gugu.domain.record.service.dto.response.RecordsCurrentResponse;
import com.bank.gugu.domain.record.service.dto.response.RecordsResponse;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.response.ApiResponse;
import com.bank.gugu.global.response.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Graph API Controller", description = "그래프 관련 API를 제공합니다.")
@RestController
@RequiredArgsConstructor
public class GraphApiController {

    private final GraphService graphService;

    @Operation(summary = "월/년별 그래프 데이터 조회 API", description = "월/년별 그래프 데이터를 조회합니다.")
    @GetMapping(value = "/api/v1/user/graphs")
    public ResponseEntity<DataResponse<GraphsResponse>> getGraph(
            @ParameterObject @ModelAttribute GraphsInput input,
            @Parameter(hidden = true) User user
    ) {
        GraphsResponse graph = graphService.getGraphs(input, user);
        return ResponseEntity.ok(DataResponse.send(graph));
    }


}
