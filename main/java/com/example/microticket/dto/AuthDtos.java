package com.example.microticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AuthDtos {

    @Data
    public static class LoginReq {
        @NotBlank
        private String username;

        @NotBlank
        private String password;
    }

    @Data
    public static class RegisterReq {
        @NotBlank
        @Size(min = 3, max = 64)
        private String username;

        @NotBlank
        @Size(min = 6, max = 128)
        private String password;

        private String email;

        /**
         * 可选：用户自定义昵称。为空时默认使用 username。
         */
        private String nickname;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginResp {
        private String token;
        private String role;
        private Long userId;
        private String username;

        /**
         * 昵称展示：nickname#nicknameCode（管理员搜索/授权用）
         */
        private String nicknameDisplay;
    }
}
