package com.example.microticket.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String email;
}
