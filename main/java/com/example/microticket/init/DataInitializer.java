package com.example.microticket.init;

import com.example.microticket.domain.User;
import com.example.microticket.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    /**
     * Create the first admin if none exists.
     * Default: admin / admin123 (CHANGE IT after first login!)
     */
    @Bean
    public CommandLineRunner ensureAdmin(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            // 1) If there is no ADMIN at all, create a default one.
            if (userRepository.countByRole("ADMIN") == 0 && !userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ADMIN");
                userRepository.save(admin);
            }

            // 2) Ensure the 'admin' account is ADMIN and its password is BCrypt.
            userRepository.findByUsername("admin").ifPresent(u -> {
                if (u.getRole() == null || !"ADMIN".equals(u.getRole())) {
                    u.setRole("ADMIN");
                }
                // If your SQL seeded a plain-text password (e.g. admin123), upgrade it.
                if (u.getPassword() == null || !u.getPassword().startsWith("$2")) {
                    u.setPassword(encoder.encode("admin123"));
                }
                // Ensure nickname / nicknameCode exists for admin
                if (u.getNickname() == null || u.getNickname().trim().isEmpty()) {
                    u.setNickname("admin");
                }
                if (u.getNicknameCode() == null || u.getNicknameCode().trim().isEmpty()) {
                    u.setNicknameCode("0000");
                }
                userRepository.save(u);
            });

            // 3) Ensure all existing users have nickname + nicknameCode (for older DBs)
            userRepository.findAll().forEach(u -> {
                boolean changed = false;
                if (u.getNickname() == null || u.getNickname().trim().isEmpty()) {
                    u.setNickname(u.getUsername() != null ? u.getUsername() : "用户" + u.getId());
                    changed = true;
                }
                if (u.getNicknameCode() == null || u.getNicknameCode().trim().isEmpty()) {
                    // generate a non-conflicting code (simple)
                    String code = String.format("%04d", (int)(Math.random()*10000));
                    int guard = 0;
                    while (userRepository.existsByNicknameCode(code) && guard++ < 30) {
                        code = String.format("%04d", (int)(Math.random()*10000));
                    }
                    if (userRepository.existsByNicknameCode(code)) {
                        code = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
                    }
                    u.setNicknameCode(code);
                    changed = true;
                }
                if (changed) userRepository.save(u);
            });
        };
    }
}
