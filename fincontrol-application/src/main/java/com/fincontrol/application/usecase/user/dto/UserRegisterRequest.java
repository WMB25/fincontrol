package com.fincontrol.application.usecase.user.dto;

public record UserRegisterRequest(
    String name,
    String email,
    String password
) {}