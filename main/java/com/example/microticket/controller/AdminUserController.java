package com.example.microticket.controller;

import com.example.microticket.dto.UserDtos;
import com.example.microticket.service.UserAdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserAdminService userAdminService;

    public AdminUserController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    public List<UserDtos.UserItem> listUsers() {
        return userAdminService.listUsers();
    }

    /**
     * 通过“昵称#标识”或 username 搜索用户，用于授权。
     * 示例：GET /api/admin/users/search?key=张三#4831
     */
    @GetMapping("/search")
    public UserDtos.UserItem search(@RequestParam("key") String key) {
        return userAdminService.searchByNicknameDisplayOrUsername(key);
    }

    @PostMapping("/{id}/grant-admin")
    public void grantAdmin(@PathVariable("id") Long id) {
        userAdminService.grantAdmin(id);
    }

    @PostMapping("/{id}/revoke-admin")
    public void revokeAdmin(@PathVariable("id") Long id) {
        userAdminService.revokeAdmin(id);
    }
}
