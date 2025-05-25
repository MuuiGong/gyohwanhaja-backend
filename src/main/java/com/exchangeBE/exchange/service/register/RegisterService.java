package com.exchangeBE.exchange.service.register;

import com.exchangeBE.exchange.dto.RegisterDto;
import com.exchangeBE.exchange.entity.User.User;
import com.exchangeBE.exchange.repository.Community.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegisterDto registerDto) {
        //  기존 회원 체크
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }


        User user = new User();

        user.setEmail(registerDto.getEmail());
        user.setNickname(registerDto.getNickname());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        userRepository.save(user);
    }
}
