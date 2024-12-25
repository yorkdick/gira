package com.rayfay.gira.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", "testSecretKeyWithAtLeast32Characters123456789");
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationInMs", 3600000);
        ReflectionTestUtils.setField(tokenProvider, "refreshExpirationInMs", 86400000);
    }

    @Test
    void testGenerateToken() {
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .authorities(Collections.emptySet())
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null,
                userPrincipal.getAuthorities());

        String token = tokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals("testuser", tokenProvider.getUsernameFromToken(token));
    }

    @Test
    void testGenerateRefreshToken() {
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .authorities(Collections.emptySet())
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null,
                userPrincipal.getAuthorities());

        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        assertNotNull(refreshToken);
        assertTrue(tokenProvider.validateToken(refreshToken));
        assertEquals("testuser", tokenProvider.getUsernameFromToken(refreshToken));
    }

    @Test
    void testValidateTokenWithInvalidSignature() {
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYxNjE1MjAwMCwiZXhwIjoxNjE2MTU1NjAwfQ.invalid_signature";
        assertFalse(tokenProvider.validateToken(invalidToken));
    }

    @Test
    void testValidateTokenWithExpiredToken() {
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationInMs", 0);
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .authorities(Collections.emptySet())
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null,
                userPrincipal.getAuthorities());

        String token = tokenProvider.generateToken(authentication);
        assertFalse(tokenProvider.validateToken(token));
    }

    @Test
    void testValidateTokenWithMalformedToken() {
        String malformedToken = "malformed.token.here";
        assertFalse(tokenProvider.validateToken(malformedToken));
    }
}