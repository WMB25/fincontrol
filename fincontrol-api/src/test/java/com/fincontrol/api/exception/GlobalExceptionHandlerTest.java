package com.fincontrol.api.exception;

import com.fincontrol.domain.exceptions.EmailAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleEmailAlreadyExistsException_shouldReturn409WithCorrectBody() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("O Email já esta cadastrado no sistema!");

        ResponseEntity<StandardError> response = handler.handleEmailAlreadyExistsException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());

        StandardError body = response.getBody();
        assertEquals(409, body.status());
        assertEquals("Conflict", body.error());
        assertEquals("O Email já esta cadastrado no sistema!", body.message());
        assertNotNull(body.timestamp());
    }
}
