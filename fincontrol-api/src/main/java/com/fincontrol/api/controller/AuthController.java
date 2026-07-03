package com.fincontrol.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fincontrol.application.usecase.user.LoginUseCase;
import com.fincontrol.application.usecase.user.dto.TokenResponse;
import com.fincontrol.application.usecase.user.dto.UserLoginRequest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final LoginUseCase loginUseCase;

    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody UserLoginRequest request) {
        TokenResponse tokenResponse = loginUseCase.execute(request);
        return ResponseEntity.ok(tokenResponse);
    }
}