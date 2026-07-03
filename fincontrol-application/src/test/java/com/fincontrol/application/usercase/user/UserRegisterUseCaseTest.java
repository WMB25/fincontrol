package com.fincontrol.application.usercase.user;

import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fincontrol.application.security.PasswordEncoderPort;
import com.fincontrol.application.usecase.user.UserRegisterUseCase;
import com.fincontrol.application.usecase.user.dto.UserRegisterRequest;
import com.fincontrol.application.usecase.user.dto.UserResponseDTO;
import com.fincontrol.domain.entity.User;
import com.fincontrol.domain.exceptions.EmailAlreadyExistsException;
import com.fincontrol.domain.repository.IUserRepository;

public class UserRegisterUseCaseTest {
    private IUserRepository userRepository;
    private PasswordEncoderPort passwordEncoderPort;
    private UserRegisterUseCase userRegisterUseCase;

    @BeforeEach
    void setUp() {
        this.userRepository = mock(IUserRepository.class);
        this.passwordEncoderPort = mock(PasswordEncoderPort.class);
        this.userRegisterUseCase = new UserRegisterUseCase(userRepository, passwordEncoderPort);
   }

   @Test
   void shouldRegisterUserSuccessfully() {
        UserRegisterRequest request = new UserRegisterRequest("Walison", "walison@email.com", "Senha156");
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoderPort.encode(request.password())).thenReturn("encrypted_senha123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO response = userRegisterUseCase.execute(request);
        
        assertNotNull(response);
        assertEquals("Walison", response.name());
        assertEquals("walison@email.com", response.email());

        verify(passwordEncoderPort, times(1)).encode("Senha156");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserRegisterRequest request = new UserRegisterRequest("Walison", "walison@email.com", "senha123");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);
        
        assertThrows(EmailAlreadyExistsException.class, () -> userRegisterUseCase.execute(request));
        
        verify(passwordEncoderPort, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}