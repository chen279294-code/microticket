package com.example.microticket.repository;

import com.example.microticket.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    Optional<User> findByOpenid(String openid);

    boolean existsByNicknameCode(String nicknameCode);

    Optional<User> findByNicknameAndNicknameCode(String nickname, String nicknameCode);

    long countByRole(String role);
}
