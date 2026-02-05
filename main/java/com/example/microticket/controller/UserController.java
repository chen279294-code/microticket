package com.example.microticket.controller;

import com.example.microticket.domain.User;
import com.example.microticket.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        Map<String, Object> map = new HashMap<>();
        map.put("authenticated", authentication != null);

        if (authentication == null) {
            return map;
        }

        // our JwtAuthFilter sets principal as: "username#userId"
        String principal = authentication.getName();
        map.put("principal", principal);
        String username = null;
        if (principal != null && principal.contains("#")) {
            String[] parts = principal.split("#", 2);
            username = parts[0];
            map.put("username", username);
            map.put("userId", parts[1]);
        }
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            // e.g. ROLE_ADMIN / ROLE_USER
            map.put("authorities", authentication.getAuthorities());
        }
        // Attach user profile (nickname / nicknameCode)
        if (username != null) {
            userRepository.findByUsername(username).ifPresent(u -> {
                map.put("nickname", u.getNickname());
                map.put("nicknameCode", u.getNicknameCode());
                if (u.getNickname() != null && u.getNicknameCode() != null) {
                    map.put("nicknameDisplay", u.getNickname() + "#" + u.getNicknameCode());
                }
                map.put("role", u.getRole());
            });
        }
        return map;
    }
}
