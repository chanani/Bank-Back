package com.bank.gugu.global.utils;

import com.bank.gugu.global.exception.OperationErrorException;
import com.bank.gugu.global.exception.dto.ErrorCode;
import com.bank.gugu.global.redis.RedisProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class MailSendUtil {

    private final MailAuthUtil mailAuthUtil;
    private final RedisProvider redisUtil;

    @Setter
    @Getter
    private String authNum;

    // 각 이메일에 대한 마지막 요청 시간을 저장할 Map
    Map<String, Long> lastRequestTimeMap = ExpiringMap.builder()
            .maxSize(1000)
            .expirationPolicy(ExpirationPolicy.CREATED)
            .expiration(180, TimeUnit.SECONDS)
            .build();

    public String welcomeMailSend(String emailInput, String authNum ) {

        Properties prop = System.getProperties();

        // 로그인시 TLS를 사용할 것인지 설정
        prop.put("mail.smtp.starttls.enable", "true");

        // 이메일 발송을 처리해줄 SMTP서버
        prop.put("mail.smtp.host", "smtp.gmail.com");

        // SMTP 서버의 인증을 사용한다는 의미
        prop.put("mail.smtp.auth", "true");

        // TLS의 포트번호는 587이며 SSL의 포트번호는 465이다.
        prop.put("mail.smtp.port", "587");

        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Authenticator auth = new MailAuthUtil();

        Session session = Session.getDefaultInstance(prop, mailAuthUtil);

        MimeMessage msg = new MimeMessage(session);

        try {
            // 보내는 날짜 지정
            msg.setSentDate(new Date());

            // 발송자를 지정한다. 발송자의 메일, 발송자명
            msg.setFrom(new InternetAddress("ohohuniverse9@gmail.com", "GUGU"));

            // 수신자의 메일을 생성한다.
            InternetAddress to = new InternetAddress(emailInput);

            // Message 클래스의 setRecipient() 메소드를 사용하여 수신자를 설정한다. setRecipient() 메소드로 수신자, 참조,
            // 숨은 참조 설정이 가능하다.
            // Message.RecipientType.TO : 받는 사람
            // Message.RecipientType.CC : 참조
            // Message.RecipientType.BCC : 숨은 참조
            msg.setRecipient(Message.RecipientType.TO, to);

            // 메일의 제목 지정
            msg.setSubject("구구 가계부에서 인증번호가 발송되었습니다.", "UTF-8");

            // Transport는 메일을 최종적으로 보내는 클래스로 메일을 보내는 부분이다.
            msg.setText("아아디 찾기를 위해 인증번호를 발송합니다. \n인증번호 : " + authNum, "UTF-8");

            Transport.send(msg);
            System.out.println(authNum);
            return "인증번호 발송에 성공하였습니다.";
        } catch (AddressException ae) {
            System.out.println("AddressException : " + ae.getMessage());
            return "인증번호 발송에 실패하였습니다.";
        } catch (MessagingException me) {
            System.out.println("MessagingException : " + me.getMessage());
            return "인증번호 발송에 실패하였습니다.";
        } catch (UnsupportedEncodingException e) {
            System.out.println("UnsupportedEncodingException : " + e.getMessage());
            return "인증번호 발송에 실패하였습니다.";
        }
    }

    /**
     * 이메일 발송
     * @param email 발송 요청 이메일
     */
    public void sendEmail(String email) {

        // 이전 요청 시간 확인
        checkSendTime(email);

        // 임의 인증번호 생성
        String authToken = createAuthToken();

        // 이메일 발송
        setAuthNum(authToken);
        String mailResult = welcomeMailSend(email, getAuthNum());

        // 실패했을 경우 예외 발생
        if(!mailResult.equals("인증번호 발송에 성공하였습니다.")){
            throw new OperationErrorException(ErrorCode.FAIL_EMAIL);
        }

        // 발송된 인증번호 redis에 저장
        saveRedis(email, authToken);
    }

    /**
     * 인증번호 생성
     */
    private static String createAuthToken() {
        Random random = new Random();

        List<String> list = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            list.add(String.valueOf(random.nextInt(10)));
        }

        for (int i = 0; i < 3; i++) {
            list.add(String.valueOf((char) (random.nextInt(26) + 65)));
        }

        Collections.shuffle(list);
        String authToken = "";
        for (String item : list) authToken += item;
        return authToken;
    }

    /**
     * 인증번호 발송 내역 Reids에 등록 : 2분
     * @param key 이메일 또는 연락처(key값)
     * @param authNumber 인증번호
     */
    private void saveRedis(String key, String authNumber) {
        redisUtil.setDataExpire(key, authNumber, 60 * 2L);
    }

    /**
     * 인증번호 발송 시간 확인(중복 요청 방지)
     * @param email 발송 요청 이메일
     */
    private void checkSendTime(String email) {
        // 이전 요청 시간 확인
        if (lastRequestTimeMap.containsKey(email)) {
            long lastRequestTime = lastRequestTimeMap.get(email);
            long currentTime = System.currentTimeMillis();

            // 시간 간격 확인 (예: 1분)
            if (currentTime - lastRequestTime < 60000) { // 1분 = 60000밀리초
                throw new OperationErrorException(ErrorCode.CHECK_EMAIL);
            }
        }

        // 새로운 요청의 시간을 저장
        lastRequestTimeMap.put(email, System.currentTimeMillis());
    }
}
