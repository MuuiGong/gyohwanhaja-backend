package com.exchangeBE.exchange.repository.Community;

import com.exchangeBE.exchange.entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 사용자 이름으로 사용자 조회
    Optional<User> findBynickname(String nickname);
    boolean existsByEmail(String email);
}
