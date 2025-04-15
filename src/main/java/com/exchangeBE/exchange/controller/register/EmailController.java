package com.exchangeBE.exchange.controller.register;

import com.exchangeBE.exchange.dto.EmailDto;
import com.exchangeBE.exchange.service.register.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailDto emailDto) {
        emailService.sendEmail(emailDto);
        // 이메일 전송 로직 구현
        return ResponseEntity.ok("이메일 전송 완료");
    }

    @PostMapping("/resend")
    public ResponseEntity<String> resendEmail(@RequestBody EmailDto emailDto) {
        // 이메일 재전송 로직 구현
        emailService.resendEmail(emailDto);
        return ResponseEntity.ok("이메일 재전송 완료");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestBody EmailDto emailDto) {
        // 이메일 인증 로직 구현
        emailService.verifyEmail(emailDto);
        return ResponseEntity.ok("이메일 인증 완료");
    }
}
