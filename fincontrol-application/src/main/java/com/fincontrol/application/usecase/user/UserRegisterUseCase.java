package com.fincontrol.application.usecase.user;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fincontrol.application.security.PasswordEncoderPort;
import com.fincontrol.application.usecase.user.dto.UserRegisterRequest;
import com.fincontrol.application.usecase.user.dto.UserResponseDTO;
import com.fincontrol.domain.entity.User;
import com.fincontrol.domain.repository.IUserRepository;

public class UserRegisterUseCase {
    private final IUserRepository userRepository;
    private final PasswordEncoderPort passwordEncoderPort;

    public UserRegisterUseCase(IUserRepository userRepository, PasswordEncoderPort passwordEncoderPort) {
        this.userRepository = userRepository;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    public UserResponseDTO execute(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email()))
        {
            throw new RuntimeException("O Email já esta cadastrado no sistema!");
        }

        User user = User.builder()
            .id(UUID.randomUUID())
            .name(request.name())
            .email(request.email())
            .password(passwordEncoderPort.encode(request.password()))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
            
        user.validate();

        User savedUser = userRepository.save(user);

        return new UserResponseDTO(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail()
        );
    }
}