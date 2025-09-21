package com.bank.gugu.global.config.dataBase;

import org.springframework.stereotype.Component;

@Component
public class TransactionContextHolder {
    private static final ThreadLocal<Boolean> readOnlyContext = new ThreadLocal<>();

    public static void setReadOnly(boolean readOnly) {
        readOnlyContext.set(readOnly);
    }

    public static boolean isReadOnly() {
        Boolean readOnly = readOnlyContext.get();
        return readOnly != null && readOnly;
    }

    public static void clear() {
        readOnlyContext.remove();
    }
}
