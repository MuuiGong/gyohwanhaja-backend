package com.exchangeBE.exchange.controller.register;

import com.exchangeBE.exchange.dto.RegisterDto;
import com.exchangeBE.exchange.service.register.RegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/register")
public class RegisterController {
    private final RegisterService registerService;
    @PostMapping
    public void register(@RequestBody RegisterDto registerDto) {
        log.info("회원가입 요청: {}, {}", registerDto.getEmail(), registerDto.getNickname());
        registerService.registerUser(registerDto);


        // 회원가입 로직 구현

    }
}
