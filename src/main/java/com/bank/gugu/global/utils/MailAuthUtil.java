package com.bank.gugu.global.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

@Component
public class MailAuthUtil extends Authenticator {

    @Value("${gugu.mail-id}")
    private String MAIL_ID;

    @Value("${gugu.mail-pw}")
    private String MAIL_PW;

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(MAIL_ID, MAIL_PW);
    }

}
