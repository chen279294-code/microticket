package com.example.microticket.controller;

import com.example.microticket.dto.AuthDtos;
import com.example.microticket.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthDtos.LoginResp login(@RequestBody @Valid AuthDtos.LoginReq req) {
        return authService.login(req);
    }

    @PostMapping("/register")
    public AuthDtos.LoginResp register(@RequestBody @Valid AuthDtos.RegisterReq req) {
        return authService.register(req);
    }
}
