package com.example.taskapi.unit.security;

import com.example.taskapi.security.JwtUtil;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.*;

class JwtUtilUnitTest {

    // Build a base64 secret that decodes to 32 bytes (all 'A' chars) to satisfy JwtUtil requirement.
    private static final String TEST_SECRET_BASE64 = Base64.getEncoder()
            .encodeToString("01234567890123456789012345678901".getBytes()); // 32 chars -> 32 bytes

    @Test
    void generate_and_validate_token() {
        JwtUtil util = new JwtUtil(TEST_SECRET_BASE64, 10000);
        String token = util.generateToken("alice");
        assertThat(token).isNotBlank();
        assertThat(util.validate(token)).isTrue();
        assertThat(util.extractUsername(token)).isEqualTo("alice");
    }

    @Test
    void validate_invalid_token_returns_false() {
        JwtUtil util = new JwtUtil(TEST_SECRET_BASE64, 10000);
        assertThat(util.validate("bad.token")).isFalse();
    }
}
