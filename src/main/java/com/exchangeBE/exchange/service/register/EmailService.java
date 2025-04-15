package com.exchangeBE.exchange.service.register;

import com.exchangeBE.exchange.dto.EmailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private final SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, StringRedisTemplate redisTemplate, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
        this.templateEngine = templateEngine;
    }

    private static final String PREFIX = "email:";
    private static final Duration TTL = Duration.ofMinutes(3);
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    public void sendEmail(EmailDto emailDto) {
        String code = generateCode();

        // Redis에 저장
        redisTemplate.opsForValue().set(PREFIX + emailDto.getEmail(), code, TTL);

        // 이메일 전송
        sendHtmlMail(emailDto.getEmail(), code);
    }

    public void resendEmail(EmailDto emailDto) {
        String redisKey = PREFIX + emailDto.getEmail();
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode != null) {
            redisTemplate.delete(redisKey);
        }

        String code = generateCode();
        redisTemplate.opsForValue().set(redisKey, code, TTL);
        sendHtmlMail(emailDto.getEmail(), code);
    }

    public void verifyEmail(EmailDto emailDto) {
        String redisKey = PREFIX + emailDto.getEmail();
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            throw new IllegalStateException("인증 코드가 만료되었거나 존재하지 않습니다.");
        }

        if (!storedCode.equals(emailDto.getVerificationCode())) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        // 인증 성공 시 Redis 키 삭제
        redisTemplate.delete(redisKey);
    }
    private String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }

    public void sendHtmlMail(String to, String code) {
        Context context = new Context();
        context.setVariable("code", code);

        String html = templateEngine.process("mail", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("이메일 인증 코드");
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }
}
