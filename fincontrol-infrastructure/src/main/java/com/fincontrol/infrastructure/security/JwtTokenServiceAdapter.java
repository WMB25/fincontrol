package com.fincontrol.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fincontrol.application.security.TokenServicePort;
import com.fincontrol.domain.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class JwtTokenServiceAdapter implements TokenServicePort {
    @Value("${api.security.token.secret:fincontrol-secret-key-super-safe}")
    private String secretKey;

    @Override
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.create()
                    .withIssuer("fincontrol-api")
                    .withSubject(user.getEmail())
                    .withExpiresAt(generateExpirationDate()) // Token expires in 2 hours
                    .sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    @Override
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.require(algorithm)
                    .withIssuer("fincontrol-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (Exception e) {
            return "";
        }
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
