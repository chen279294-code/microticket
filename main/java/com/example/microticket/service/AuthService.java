package com.example.microticket.service;

import com.example.microticket.dto.AuthDtos;
import com.example.microticket.domain.User;
import com.example.microticket.repository.UserRepository;
import com.example.microticket.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthDtos.LoginResp login(AuthDtos.LoginReq req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("账号或密码错误"));

        // IMPORTANT: your old SQL had plain text passwords. We only accept BCrypt by default.
        // If you still have plain text rows, update them to BCrypt.
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("账号或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new AuthDtos.LoginResp(token, user.getRole(), user.getId(), user.getUsername(), nicknameDisplay(user));
    }

    /**
     * Register defaults to USER role.
     * For convenience (web/mini-program), return a token after successful registration.
     */
    public AuthDtos.LoginResp register(AuthDtos.RegisterReq req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setRole("USER");
        String nickname = (req.getNickname() == null || req.getNickname().trim().isEmpty())
                ? req.getUsername()
                : req.getNickname().trim();
        user.setNickname(nickname);
        user.setNicknameCode(generateNicknameCode());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new AuthDtos.LoginResp(token, user.getRole(), user.getId(), user.getUsername(), nicknameDisplay(user));
    }

    private String nicknameDisplay(User user) {
        String nick = user.getNickname() == null ? "" : user.getNickname();
        String code = user.getNicknameCode() == null ? "" : user.getNicknameCode();
        if (nick.isEmpty() && code.isEmpty()) return null;
        if (code.isEmpty()) return nick;
        return nick + "#" + code;
    }

    /**
     * 生成一个较短的“昵称标识”，全库唯一。
     * 先尝试 4 位数字（0000-9999）若冲突则多次重试，最终兜底用 UUID 前缀。
     */
    private String generateNicknameCode() {
        for (int i = 0; i < 30; i++) {
            int v = (int) (Math.random() * 10000);
            String code = String.format("%04d", v);
            if (!userRepository.existsByNicknameCode(code)) {
                return code;
            }
        }
        // fallback
        String code = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        while (userRepository.existsByNicknameCode(code)) {
            code = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        }
        return code;
    }
}
