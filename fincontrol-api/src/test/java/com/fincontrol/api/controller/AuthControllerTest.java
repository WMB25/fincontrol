package com.fincontrol.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fincontrol.application.usecase.user.LoginUseCase;
import com.fincontrol.application.usecase.user.dto.TokenResponse;
import com.fincontrol.application.usecase.user.dto.UserLoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;
    private LoginUseCase loginUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        loginUseCase = mock(LoginUseCase.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(loginUseCase))
                .build();
    }

    // ─── POST /api/v1/auth/login ──────────────────────────────────────────────

    @Test
    void login_shouldReturn200WithTokenWhenCredentialsAreValid() throws Exception {
        UserLoginRequest request = new UserLoginRequest("walison@email.com", "senha123");
        TokenResponse tokenResponse = new TokenResponse("jwt-token-mock", "Bearer");

        when(loginUseCase.execute(any(UserLoginRequest.class))).thenReturn(tokenResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-mock"))
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    void login_shouldThrowWhenCredentialsAreInvalid() {
        UserLoginRequest request = new UserLoginRequest("invalido@email.com", "senha_errada");

        when(loginUseCase.execute(any(UserLoginRequest.class)))
                .thenThrow(new RuntimeException("Credenciais inválidas."));

        // No Spring 7 / Spring Boot 4, RuntimeException não tratada é propagada pelo MockMvc
        Exception ex = assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
        );

        assertTrue(ex.getCause() instanceof RuntimeException
                || ex instanceof RuntimeException);
    }
}
