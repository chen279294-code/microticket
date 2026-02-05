package com.example.microticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class UserDtos {
    @Data
    @AllArgsConstructor
    public static class UserItem {
        private Long id;
        private String username;
        private String nickname;
        private String nicknameCode;
        private String nicknameDisplay;
        private String role;
        private String email;
    }
}
