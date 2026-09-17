package com.aireview.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JwtUtilTest {
    private static final String SECRET = "unit-test-secret-with-enough-length-32";
    private final JwtUtil jwtUtil = new JwtUtil(SECRET, 1);

    @Test
    void generatedTokenCarriesUserId() {
        String token = jwtUtil.generateToken(42L);

        assertThat(jwtUtil.parseUserId(token)).isEqualTo(42L);
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        String token = new JwtUtil("another-secret-with-enough-length-32b", 1).generateToken(42L);

        assertThat(jwtUtil.parseUserId(token)).isNull();
    }

    @Test
    void tamperedTokenIsRejected() {
        String token = jwtUtil.generateToken(42L);

        assertThat(jwtUtil.parseUserId(token + "x")).isNull();
    }

    @Test
    void malformedTokenIsRejected() {
        assertThat(jwtUtil.parseUserId("not-a-jwt")).isNull();
    }

    @Test
    void expiredTokenIsRejected() {
        JwtUtil expiring = new JwtUtil(SECRET, -1);
        String token = expiring.generateToken(42L);

        assertThat(expiring.parseUserId(token)).isNull();
    }
}
