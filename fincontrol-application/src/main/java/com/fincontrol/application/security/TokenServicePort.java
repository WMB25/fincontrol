package com.fincontrol.application.security;

import com.fincontrol.domain.entity.User;

public interface TokenServicePort {
    String generateToken(User user);
    String validateToken(String token);
}
