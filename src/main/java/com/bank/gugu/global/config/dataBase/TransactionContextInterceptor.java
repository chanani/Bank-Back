package com.bank.gugu.global.config.dataBase;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Aspect
@Order(0)
public class TransactionContextInterceptor {

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object setTransactionContext(ProceedingJoinPoint joinPoint) throws Throwable {
        // 메서드의 @Transactional 어노테이션 정보 가져오기
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Transactional transactional = signature.getMethod().getAnnotation(Transactional.class);

        if (transactional == null) {
            // 클래스 레벨 어노테이션 확인
            transactional = joinPoint.getTarget().getClass().getAnnotation(Transactional.class);
        }

        try {
            if (transactional != null) {
                TransactionContextHolder.setReadOnly(transactional.readOnly());
                System.out.println("=== AOP에서 설정 ===");
                System.out.println("ReadOnly: " + transactional.readOnly());
                System.out.println("==================");
            }
            return joinPoint.proceed();
        } finally {
            TransactionContextHolder.clear();
        }
    }
}