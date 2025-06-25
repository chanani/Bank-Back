package com.bank.gugu.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

/**
 * EventListener이 비동기로 수행될 수 있도록 설정
 * '@EventListener'가 붙은 메서드는 비동기로 작동
 * '@TransactionalEventListener'가 붙은 메서드는 '@Async'를 추가해 비동기로 작동
 */
@Configuration
public class AsynchronousEventConfig {

    @Bean
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster
                = new SimpleApplicationEventMulticaster();

        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());

        return eventMulticaster;
    }

}
