package com.fincontrol.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fincontrol.application.usecase.user.UserRegisterUseCase;
import com.fincontrol.application.usecase.user.dto.UserRegisterRequest;
import com.fincontrol.application.usecase.user.dto.UserResponseDTO;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserRegisterUseCase userRegisterUseCase;

    public UserController(UserRegisterUseCase userRegisterUseCase) {
        this.userRegisterUseCase = userRegisterUseCase;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRegisterRequest request) {
        UserResponseDTO response = userRegisterUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}