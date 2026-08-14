package com.fincontrol.infrastructure.security;

import com.fincontrol.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceAdapterTest {

    private JwtTokenServiceAdapter tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new JwtTokenServiceAdapter();
        ReflectionTestUtils.setField(tokenService, "secretKey", "test-secret-key-fincontrol-2026-very-long-string");
        ReflectionTestUtils.setField(tokenService, "expirationHours", 2);
    }

    private User buildUser() {
        return User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .name("Walison")
                .email("walison@email.com")
                .password("senha_encoded")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ─── generateToken ────────────────────────────────────────────────────────

    @Test
    void generateToken_shouldReturnNonNullToken() {
        String token = tokenService.generateToken(buildUser());
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void generateToken_shouldReturnJwtWithThreeParts() {
        String token = tokenService.generateToken(buildUser());
        // JWT possui exatamente 3 partes separadas por ponto
        assertEquals(3, token.split("\\.").length);
    }

    // ─── validateToken ────────────────────────────────────────────────────────

    @Test
    void validateToken_shouldReturnUserIdSubjectForValidToken() {
        User user = buildUser();
        String token = tokenService.generateToken(user);

        String subject = tokenService.validateToken(token);

        assertEquals(user.getId().toString(), subject);
    }

    @Test
    void validateToken_shouldReturnNullForInvalidToken() {
        String subject = tokenService.validateToken("token.invalido.assinatura");
        assertNull(subject);
    }

    @Test
    void validateToken_shouldReturnNullForTamperedToken() {
        User user = buildUser();
        String validToken = tokenService.generateToken(user);
        // Adultera a assinatura
        String tampered = validToken.substring(0, validToken.lastIndexOf('.') + 1) + "assinaturafalsa";

        String subject = tokenService.validateToken(tampered);
        assertNull(subject);
    }

    @Test
    void validateToken_shouldReturnNullForEmptyString() {
        assertNull(tokenService.validateToken(""));
    }
}
