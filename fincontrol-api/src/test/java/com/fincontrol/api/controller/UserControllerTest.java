package com.fincontrol.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fincontrol.api.exception.GlobalExceptionHandler;
import com.fincontrol.application.usecase.user.UserRegisterUseCase;
import com.fincontrol.application.usecase.user.dto.UserRegisterRequest;
import com.fincontrol.application.usecase.user.dto.UserResponseDTO;
import com.fincontrol.domain.exceptions.EmailAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;
    private UserRegisterUseCase userRegisterUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        userRegisterUseCase = mock(UserRegisterUseCase.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController(userRegisterUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ─── POST /api/v1/users ───────────────────────────────────────────────────

    @Test
    void registerUser_shouldReturn201WhenRequestIsValid() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest("Walison", "walison@email.com", "Senha156");
        UserResponseDTO response = new UserResponseDTO(UUID.randomUUID(), "Walison", "walison@email.com");

        when(userRegisterUseCase.execute(any(UserRegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Walison"))
                .andExpect(jsonPath("$.email").value("walison@email.com"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void registerUser_shouldReturn409WhenEmailAlreadyExists() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest("Walison", "walison@email.com", "Senha156");

        when(userRegisterUseCase.execute(any(UserRegisterRequest.class)))
                .thenThrow(new EmailAlreadyExistsException("O Email já esta cadastrado no sistema!"));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("O Email já esta cadastrado no sistema!"));
    }
}
