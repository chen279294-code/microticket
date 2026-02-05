package com.example.microticket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSummary {
    private Long id;
    private String username;
    private String role;
    private String email;
}
