package com.fincontrol.application.usercase.user;

import com.fincontrol.application.security.PasswordEncoderPort;
import com.fincontrol.application.security.TokenServicePort;
import com.fincontrol.application.usecase.user.dto.TokenResponse;
import com.fincontrol.application.usecase.user.dto.UserLoginRequest;
import com.fincontrol.domain.entity.User;
import com.fincontrol.domain.repository.IUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fincontrol.application.usecase.user.LoginUseCase;

class LoginUseCaseTest {

    private IUserRepository userRepository;
    private PasswordEncoderPort passwordEncoderPort;
    private TokenServicePort tokenServicePort;
    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(IUserRepository.class);
        passwordEncoderPort = mock(PasswordEncoderPort.class);
        tokenServicePort = mock(TokenServicePort.class);
        loginUseCase = new LoginUseCase(userRepository, passwordEncoderPort, tokenServicePort);
    }

    @Test
    void shouldLoginSuccessfullyAndReturnToken() {
        // Arrange
        UserLoginRequest request = new UserLoginRequest("walison@email.com", "senha123");
        User user = new User(UUID.randomUUID(), "Walison", "walison@email.com", "encrypted_password", LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches(request.password(), user.getPassword())).thenReturn(true);
        when(tokenServicePort.generateToken(user)).thenReturn("jwt-mock-token");

        // Act
        TokenResponse response = loginUseCase.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-mock-token", response.token());
        assertEquals("Bearer", response.type());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        UserLoginRequest request = new UserLoginRequest("invalido@email.com", "senha123");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> loginUseCase.execute(request));
        assertEquals("Credenciais inválidas.", exception.getMessage());
        
        verify(passwordEncoderPort, never()).matches(anyString(), anyString());
        verify(tokenServicePort, never()).generateToken(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        // Arrange
        UserLoginRequest request = new UserLoginRequest("walison@email.com", "senha_errada");
        User user = new User(UUID.randomUUID(), "Walison", "walison@email.com", "encrypted_password", LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches(request.password(), user.getPassword())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> loginUseCase.execute(request));
        assertEquals("Credenciais inválidas.", exception.getMessage());
        
        verify(tokenServicePort, never()).generateToken(any(User.class));
    }
}