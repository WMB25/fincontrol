package com.fincontrol.application.usercase.user;

import com.fincontrol.application.security.PasswordEncoderPort;
import com.fincontrol.application.usecase.user.UserRegisterUseCase;
import com.fincontrol.application.usecase.user.dto.UserRegisterRequest;
import com.fincontrol.application.usecase.user.dto.UserResponseDTO;
import com.fincontrol.domain.entity.User;
import com.fincontrol.domain.exceptions.EmailAlreadyExistsException;
import com.fincontrol.domain.repository.IUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserRegisterUseCaseTest {

    private IUserRepository userRepository;
    private PasswordEncoderPort passwordEncoderPort;
    private UserRegisterUseCase userRegisterUseCase;

    @BeforeEach
    void setUp() {
        this.userRepository = mock(IUserRepository.class);
        this.passwordEncoderPort = mock(PasswordEncoderPort.class);
        this.userRegisterUseCase = new UserRegisterUseCase(userRepository, passwordEncoderPort);
    }

    // ─── caminho feliz ────────────────────────────────────────────────────────

    @Test
    void shouldRegisterUserSuccessfully() {
        UserRegisterRequest request = new UserRegisterRequest("Walison", "walison@email.com", "Senha156");

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoderPort.encode(request.password())).thenReturn("encrypted_senha123");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO response = userRegisterUseCase.execute(request);

        assertNotNull(response);
        assertEquals("Walison", response.name());
        assertEquals("walison@email.com", response.email());

        verify(passwordEncoderPort, times(1)).encode("Senha156");
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ─── e-mail duplicado ─────────────────────────────────────────────────────

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserRegisterRequest request = new UserRegisterRequest("Walison", "walison@email.com", "senha123");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userRegisterUseCase.execute(request));

        verify(passwordEncoderPort, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    // ─── validação de campos ──────────────────────────────────────────────────

    @Test
    void shouldThrowWhenNameIsNull() {
        UserRegisterRequest request = new UserRegisterRequest(null, "walison@email.com", "senha123");

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("enc");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // validate() é chamado APÓS o save; o User.builder popula os campos antes
        // O nome null dispara validate()
        assertThrows(IllegalArgumentException.class, () -> userRegisterUseCase.execute(request));
    }

    @Test
    void shouldThrowWhenNameIsEmpty() {
        UserRegisterRequest request = new UserRegisterRequest("", "walison@email.com", "senha123");

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("enc");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThrows(IllegalArgumentException.class, () -> userRegisterUseCase.execute(request));
    }

    @Test
    void shouldThrowWhenEmailIsEmpty() {
        UserRegisterRequest request = new UserRegisterRequest("Walison", "", "senha123");

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("enc");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThrows(IllegalArgumentException.class, () -> userRegisterUseCase.execute(request));
    }

    @Test
    void shouldThrowWhenPasswordIsEmpty() {
        UserRegisterRequest request = new UserRegisterRequest("Walison", "walison@email.com", "");

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoderPort.encode("")).thenReturn("");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThrows(IllegalArgumentException.class, () -> userRegisterUseCase.execute(request));
    }
}
