package com.fincontrol.api.config;

import com.fincontrol.application.usecase.user.UserRegisterUseCase;
import com.fincontrol.domain.repository.IUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {
    @Bean
    public UserRegisterUseCase userRegisterUseCase(IUserRepository userRepository) {
        return new UserRegisterUseCase(userRepository);
    }
}