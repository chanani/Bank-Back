package com.bank.gugu.controller.common;


import com.bank.gugu.global.annotation.NoneAuth;
import com.bank.gugu.global.exception.dto.ErrorCode;
import com.bank.gugu.global.exception.dto.ErrorCodeData;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Tag(name = "Common API Controller", description = "공용으로 사용하는 API를 제공합니다.")
@Controller
public class CommonController {

    @NoneAuth
    @GetMapping("/api/v1/none/error-codes")
    public String getErrorCodes(Model model) {
        List<ErrorCodeData> errorCodes = ErrorCode.getErrorCodes();

        List<Object> httpErrorCodes = new ArrayList<>(
                Arrays.asList(
                        ErrorCodeData.error(400, "Bad Request", "클라이언트의 잘못된 요청으로 인한 에러"),
                        ErrorCodeData.error(404, "Not Found", "찾을 수 없음"),
                        ErrorCodeData.error(405, "Method Not Allowed", "리소스가 이 메서드를 지원하지 않음"),
                        ErrorCodeData.error(500, "Internal Server Error", "예기치 못한 에러"),
                        ErrorCodeData.error(502, "Bad Gateway", "게이트웨이 또는 프록시 역할을 하는 서버가 통신 체인의 다른 서버로부터 유효하지 않거나 잘못된 응답을 받음")
                )
        );


        model.addAttribute("errorCodes", errorCodes);
        model.addAttribute("httpCodes", httpErrorCodes);
        return "error-codes";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

}
