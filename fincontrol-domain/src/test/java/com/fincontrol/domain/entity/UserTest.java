package com.fincontrol.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User buildValidUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .name("Walison")
                .email("walison@email.com")
                .password("senha123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ─── validate() ──────────────────────────────────────────────────────────

    @Test
    void validate_shouldPassWhenAllFieldsAreValid() {
        User user = buildValidUser();
        assertDoesNotThrow(user::validate);
    }

    @Test
    void validate_shouldThrowWhenNameIsNull() {
        User user = buildValidUser();
        user.setName(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, user::validate);
        assertEquals("Nome não pode ser nulo ou vazio.", ex.getMessage());
    }

    @Test
    void validate_shouldThrowWhenNameIsEmpty() {
        User user = buildValidUser();
        user.setName("");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, user::validate);
        assertEquals("Nome não pode ser nulo ou vazio.", ex.getMessage());
    }

    @Test
    void validate_shouldThrowWhenEmailIsNull() {
        User user = buildValidUser();
        user.setEmail(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, user::validate);
        assertEquals("Email não pode ser nulo ou vazio.", ex.getMessage());
    }

    @Test
    void validate_shouldThrowWhenEmailIsEmpty() {
        User user = buildValidUser();
        user.setEmail("");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, user::validate);
        assertEquals("Email não pode ser nulo ou vazio.", ex.getMessage());
    }

    @Test
    void validate_shouldThrowWhenPasswordIsNull() {
        User user = buildValidUser();
        user.setPassword(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, user::validate);
        assertEquals("Senha não pode ser nula ou vazia.", ex.getMessage());
    }

    @Test
    void validate_shouldThrowWhenPasswordIsEmpty() {
        User user = buildValidUser();
        user.setPassword("");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, user::validate);
        assertEquals("Senha não pode ser nula ou vazia.", ex.getMessage());
    }

    // ─── updateInformation() ─────────────────────────────────────────────────

    @Test
    void updateInformation_shouldUpdateNameAndEmail() {
        User user = buildValidUser();
        LocalDateTime before = user.getUpdatedAt();

        user.updateInformation("Novo Nome", "novo@email.com");

        assertEquals("Novo Nome", user.getName());
        assertEquals("novo@email.com", user.getEmail());
        assertNotNull(user.getUpdatedAt());
        // updatedAt deve ter sido renovado
        assertFalse(user.getUpdatedAt().isBefore(before));
    }

    @Test
    void updateInformation_shouldThrowWhenNewNameIsNull() {
        User user = buildValidUser();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> user.updateInformation(null, "valido@email.com")
        );
        assertEquals("Nome não pode ser nulo ou vazio.", ex.getMessage());
    }

    @Test
    void updateInformation_shouldThrowWhenNewEmailIsEmpty() {
        User user = buildValidUser();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> user.updateInformation("Nome Válido", "")
        );
        assertEquals("Email não pode ser nulo ou vazio.", ex.getMessage());
    }
}
