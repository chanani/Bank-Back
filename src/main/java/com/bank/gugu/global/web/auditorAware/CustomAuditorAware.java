package com.bank.gugu.global.web.auditorAware;

import com.bank.gugu.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * CreatedBy or LastModifiedBy 에 변경한 사용자 식별자 매핑
 * */
@Component
@Slf4j
public class CustomAuditorAware implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        log.info("CustomAuditorAware.getCurrentAuditor() 실행");
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr != null) {
            HttpServletRequest request = attr.getRequest();
            Object user = request.getAttribute("loginUser");
            if (user instanceof User u) {
                log.info("CustomAuditorAware USER : {}", u.getId());
                return Optional.ofNullable(u.getId());
            }
        }
        return Optional.empty();
    }
}
