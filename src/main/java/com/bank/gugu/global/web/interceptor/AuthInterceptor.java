package com.bank.gugu.global.web.interceptor;

import com.bank.gugu.entity.common.constant.UserType;
import com.bank.gugu.entity.user.User;
import com.bank.gugu.global.annotation.NoneAuth;
import com.bank.gugu.global.exception.AccountTokenException;
import com.bank.gugu.global.exception.ForbiddenException;
import com.bank.gugu.global.exception.dto.ErrorCode;
import com.bank.gugu.global.jwt.JWTProvider;
import com.bank.gugu.global.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JWTProvider jwtProvider;
    private final ObjectMapper objectMapper;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("authInterceptor preHandle {}", request.getRequestURI());

        if (handler instanceof HandlerMethod method) {
            NoneAuth noneAuth = method.getMethodAnnotation(NoneAuth.class);
            // @NoneAuth 어노테이션이 존재하는 경우 인증 X
            if (noneAuth != null) {
                return true;
            }

            /* SecurityContext에서 인증 정보 추출
             * 정상일 경우 CustomUserDetails 반환
             * Token이 인증되지 않으면 AnonymousAuthenticationToken 반환 */
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // auth가 없을 경우, 또는 anonymous 객체일 경우 예외 발생
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                throw new AccountTokenException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
            }

            // CustomUserDetails 객체로 형변환
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            // 회원 & 관리자 정보 조회
            getUserOrOperatorInfo(userDetails, request);
        }
        return true;
    }

    /**
     * 회원 & 관리자 정보 조회
     */
    private void getUserOrOperatorInfo(CustomUserDetails userDetails, HttpServletRequest request) {
        List<String> roleList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        if (roleList.contains(UserType.USER.getRole())) {
            // 회원일 경우
            // 회원 정보 반환
            User user = jwtProvider.getUserInfo(userDetails.getId());
            request.setAttribute("loginUser", user);
        }

    }

    /**
     * 인증 토큰 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        // AUTHORIZATION 헤더가 존재하면서 Bearer 토큰인 경우 순수 토큰 반환
        if (!ObjectUtils.isEmpty(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7).trim();
        }

        throw new ForbiddenException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
    }


}
