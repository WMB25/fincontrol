package com.fincontrol.infrastructure.security;

import com.fincontrol.domain.entity.User;
import com.fincontrol.domain.repository.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityFilterTest {

    private JwtTokenServiceAdapter tokenService;
    private IUserRepository userRepository;
    private SecurityFilter securityFilter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        tokenService = mock(JwtTokenServiceAdapter.class);
        userRepository = mock(IUserRepository.class);
        securityFilter = new SecurityFilter(tokenService, userRepository);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        // Limpa o contexto de segurança entre os testes
        SecurityContextHolder.clearContext();
    }

    // ─── sem token ────────────────────────────────────────────────────────────

    @Test
    void doFilterInternal_shouldContinueChainWithoutAuthWhenNoHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_shouldContinueChainWithoutAuthWhenHeaderHasWrongPrefix() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // ─── token inválido ───────────────────────────────────────────────────────

    @Test
    void doFilterInternal_shouldContinueChainWithoutAuthWhenTokenIsInvalid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token.invalido");
        when(tokenService.validateToken("token.invalido")).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userRepository, never()).findById(any(UUID.class));
    }

    // ─── token válido ─────────────────────────────────────────────────────────

    @Test
    void doFilterInternal_shouldSetAuthenticationWhenTokenIsValid() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .name("Walison")
                .email("walison@email.com")
                .password("encoded")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(tokenService.validateToken("valid.jwt.token")).thenReturn(userId.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("walison@email.com",
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilterInternal_shouldContinueChainWhenUserNotFoundForValidToken() throws Exception {
        UUID userId = UUID.randomUUID();

        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(tokenService.validateToken("valid.jwt.token")).thenReturn(userId.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
