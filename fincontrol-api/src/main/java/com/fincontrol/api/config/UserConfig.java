package com.fincontrol.api.config;

import com.fincontrol.application.security.PasswordEncoderPort;
import com.fincontrol.application.security.TokenServicePort;
import com.fincontrol.application.usecase.user.LoginUseCase;
import com.fincontrol.application.usecase.user.UserRegisterUseCase;
import com.fincontrol.domain.repository.IUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {
    @Bean
    public UserRegisterUseCase userRegisterUseCase(IUserRepository userRepository, PasswordEncoderPort passwordEncoderPort) {
        return new UserRegisterUseCase(userRepository, passwordEncoderPort);
    }

    @Bean
    public LoginUseCase loginUseCase(IUserRepository userRepository, PasswordEncoderPort passwordEncoderPort, TokenServicePort tokenServicePort) {
        return new LoginUseCase(userRepository, passwordEncoderPort, tokenServicePort);
    }
}
