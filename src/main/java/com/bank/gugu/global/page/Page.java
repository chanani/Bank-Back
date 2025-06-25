package com.bank.gugu.global.page;

public record Page<T>(
        Pageable pageable,
        T data
) {
}
