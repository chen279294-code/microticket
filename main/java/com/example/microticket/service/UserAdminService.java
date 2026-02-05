package com.example.microticket.service;

import com.example.microticket.dto.UserDtos;
import com.example.microticket.domain.User;
import com.example.microticket.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAdminService {

    private final UserRepository userRepository;

    public UserAdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDtos.UserItem> listUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserDtos.UserItem(
                        u.getId(),
                        u.getUsername(),
                        u.getNickname(),
                        u.getNicknameCode(),
                        nicknameDisplay(u),
                        u.getRole(),
                        u.getEmail()))
                .collect(Collectors.toList());
    }

    public void grantAdmin(Long userId) {
        requireSuperAdmin();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            // admin 自己不需要授权/取消
            return;
        }
        user.setRole("ADMIN");
        userRepository.save(user);
    }

    public void revokeAdmin(Long userId) {
        requireSuperAdmin();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            // admin 不能被降级
            throw new AccessDeniedException("admin 账号不能被取消管理员");
        }
        user.setRole("USER");
        userRepository.save(user);
    }

    public UserDtos.UserItem searchByNicknameDisplayOrUsername(String key) {
        requireSuperAdmin();
        if (key == null || key.trim().isEmpty()) {
            throw new RuntimeException("请输入要搜索的昵称标识或用户名");
        }
        String k = key.trim();
        User u;
        if (k.contains("#")) {
            String[] parts = k.split("#", 2);
            String nickname = parts[0];
            String code = parts[1];
            u = userRepository.findByNicknameAndNicknameCode(nickname, code)
                    .orElseThrow(() -> new RuntimeException("未找到用户：" + k));
        } else {
            u = userRepository.findByUsername(k)
                    .orElseThrow(() -> new RuntimeException("未找到用户：" + k));
        }
        return new UserDtos.UserItem(
                u.getId(),
                u.getUsername(),
                u.getNickname(),
                u.getNicknameCode(),
                nicknameDisplay(u),
                u.getRole(),
                u.getEmail());
    }

    private String nicknameDisplay(User user) {
        String nick = user.getNickname() == null ? "" : user.getNickname();
        String code = user.getNicknameCode() == null ? "" : user.getNicknameCode();
        if (nick.isEmpty() && code.isEmpty()) return null;
        if (code.isEmpty()) return nick;
        return nick + "#" + code;
    }

    /**
     * 只有 username=admin 的管理员账号才允许做“授权/取消授权/搜索授权”操作。
     */
    private void requireSuperAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new AccessDeniedException("未登录");
        String principal = auth.getName();
        // JwtAuthFilter sets principal as: "username#userId"
        String username = principal;
        if (principal != null && principal.contains("#")) {
            username = principal.split("#", 2)[0];
        }
        if (!"admin".equalsIgnoreCase(username)) {
            throw new AccessDeniedException("只有 admin 账号可以授权/取消授权管理员");
        }
    }
}
