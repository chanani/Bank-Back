package com.bank.gugu.global.security;

import com.bank.gugu.global.jwt.JWTProvider;
import com.bank.gugu.global.jwt.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Order(0)
@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;
    private static final String[] EXCLUDE_PATHS = {
            "/api/v1/none/login",
            "/api/v1/none/operator-login"
    };

    /**
     * EXCLUDE_PATHS의 경로는 doFilterInternal 제외
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)  {
        String path = request.getRequestURI();
        return Arrays.stream(EXCLUDE_PATHS).anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // Token 추출
        String token = parseBearerToken(request);
        // 토큰값이 유요하다면 검증을 시작한다.
        if (token != null) {
            // 토큰 검증
            jwtProvider.verifyToken(TokenType.ACCESS_TOKEN, token);
            Authentication authentication = jwtProvider.getAuthentication(token);

            // SecurityContextHolder => 인증정보를 담는다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security Context에 {} 인증 정보를 저장 ", authentication.getPrincipal());

        } else {
            log.info("유효한 JWT 토큰이 없습니다, uri: {}", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Authorization Bearer 제거(공백포함 7글자)
     *
     * @param request 요청 request
     * @return token (없는경우 null)
     */
    private String parseBearerToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(token -> token.length() >= 7 && token.substring(0, 7).equalsIgnoreCase("Bearer "))
                .map(token -> token.substring(7))
                .orElse(null);
    }
}
