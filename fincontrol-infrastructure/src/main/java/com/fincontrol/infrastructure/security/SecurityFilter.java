package com.fincontrol.infrastructure.security;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fincontrol.domain.repository.IUserRepository;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private final JwtTokenServiceAdapter tokenService;
    private final IUserRepository userRepository;

    public SecurityFilter(JwtTokenServiceAdapter tokenService, IUserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = recoverToken(request);

        if(token != null) {
            var userIdStr = tokenService.validateToken(token);
            if(userIdStr != null) {
                var userId = UUID.fromString(userIdStr);
                userRepository.findById(userId).ifPresent(user -> {
                    UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities(Collections.emptyList())
                        .build();

                    var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }
}
