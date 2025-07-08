package com.bank.gugu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class BankBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankBackApplication.class, args);
    }

}
