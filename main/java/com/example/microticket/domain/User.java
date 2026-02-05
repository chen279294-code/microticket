package com.example.microticket.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User 用户实体
 * 支持：
 * USER   - 小程序普通用户
 * ADMIN  - 系统管理员
 * CINEMA - 影院管理员（可扩展）
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名（后台 / 管理员登录）
     */
    @Column(unique = true, length = 64)
    private String username;

    /**
     * 密码（当前为明文，演示用）
     */
    @Column(length = 128)
    private String password;

    /**
     * 邮箱（可选）
     */
    private String email;

    /**
     * 微信 openid（小程序登录）
     */
    @Column(unique = true, length = 128)
    private String openid;

    /**
     * 昵称
     */
    @Column(length = 128)
    private String nickname;

    /**
     * 昵称唯一标识（用于管理员通过“昵称#标识”精准搜索授权）
     * 例：nickname = 张三, nicknameCode = 4831 -> 昵称展示为：张三#4831
     */
    @Column(name = "nickname_code", length = 16, unique = true)
    private String nicknameCode;

    /**
     * 角色：
     * USER / ADMIN / CINEMA
     */
    @Column(nullable = false, length = 32)
    private String role = "USER";

    /**
     * 创建时间（由数据库生成）
     */
    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    public User() {}

    /* ---------- Getter / Setter ---------- */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 注意：当前为演示版，未加密
     * 正式项目请使用 BCryptPasswordEncoder
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNicknameCode() {
        return nicknameCode;
    }

    public void setNicknameCode(String nicknameCode) {
        this.nicknameCode = nicknameCode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
