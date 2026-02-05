package com.example.microticket.service;

import com.example.microticket.dto.UserDtos;
import com.example.microticket.domain.User;
import com.example.microticket.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDtos.UserItem> listUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserDtos.UserItem(
                        u.getId(),
                        u.getUsername(),
                        u.getNickname(),
                        u.getNicknameCode(),
                        (u.getNickname() != null && u.getNicknameCode() != null) ? (u.getNickname() + "#" + u.getNicknameCode()) : u.getNickname(),
                        u.getRole(),
                        u.getEmail()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void grantAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setRole("ADMIN");
        userRepository.save(user);
    }

    @Transactional
    public void revokeAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setRole("USER");
        userRepository.save(user);
    }
}
