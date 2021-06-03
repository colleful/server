package com.colleful.server.global.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JwtSecurityTest {

    @InjectMocks
    JwtProvider provider;

    @BeforeEach
    void setUp() {
        provider.setSecretKey("aaa123");
    }

    @Test
    void 토큰_생성() {
        String email = "test@test.com";
        Long id = 1L;
        String role = "ROLE_USER";

        String token = JwtProperties.TYPE + " " + provider.createToken(email, id, role);

        assertThat(provider.isValidateToken(token)).isTrue();
    }
}
