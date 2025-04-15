package com.exchangeBE.exchange.controller.register;

import com.exchangeBE.exchange.dto.RegisterDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class RegisterController {
    @PostMapping
    public void register(@RequestBody RegisterDto registerDto) {

        // 회원가입 로직 구현

    }
}
