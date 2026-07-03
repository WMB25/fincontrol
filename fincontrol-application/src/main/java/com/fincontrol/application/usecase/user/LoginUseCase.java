package com.fincontrol.application.usecase.user;

import com.fincontrol.application.security.PasswordEncoderPort;
import com.fincontrol.application.security.TokenServicePort;
import com.fincontrol.application.usecase.user.dto.TokenResponse;
import com.fincontrol.application.usecase.user.dto.UserLoginRequest;
import com.fincontrol.domain.entity.User;
import com.fincontrol.domain.repository.IUserRepository;

public class LoginUseCase {
    private final IUserRepository userRepository;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenServicePort tokenServicePort;

    public LoginUseCase(IUserRepository userRepository, PasswordEncoderPort passwordEncoderPort, TokenServicePort tokenServicePort) {
        this.userRepository = userRepository;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenServicePort = tokenServicePort;
    }
    
    public TokenResponse execute(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new RuntimeException("Credenciais inválidas."));
        if(!passwordEncoderPort.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Credenciais inválidas.");
        }
        String token = tokenServicePort.generateToken(user);
        return new TokenResponse(token, "Bearer");
    }

    
}
