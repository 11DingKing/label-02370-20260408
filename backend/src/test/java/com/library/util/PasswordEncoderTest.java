package com.library.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PasswordEncoderTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("密码加密")
    void testEncode() {
        String rawPassword = "password123";
        
        String encoded = passwordEncoder.encode(rawPassword);

        assertNotNull(encoded);
        assertNotEquals(rawPassword, encoded);
        assertTrue(encoded.startsWith("$SALT$"));
    }

    @Test
    @DisplayName("密码加密 - 每次结果不同")
    void testEncode_DifferentResults() {
        String rawPassword = "password123";
        
        String encoded1 = passwordEncoder.encode(rawPassword);
        String encoded2 = passwordEncoder.encode(rawPassword);

        assertNotEquals(encoded1, encoded2);
    }

    @Test
    @DisplayName("密码验证 - 正确")
    void testMatches_Correct() {
        String rawPassword = "password123";
        String encoded = passwordEncoder.encode(rawPassword);

        boolean matches = passwordEncoder.matches(rawPassword, encoded);

        assertTrue(matches);
    }

    @Test
    @DisplayName("密码验证 - 错误")
    void testMatches_Wrong() {
        String rawPassword = "password123";
        String encoded = passwordEncoder.encode(rawPassword);

        boolean matches = passwordEncoder.matches("wrongpassword", encoded);

        assertFalse(matches);
    }

    @Test
    @DisplayName("密码验证 - 空密码")
    void testMatches_NullEncoded() {
        boolean matches = passwordEncoder.matches("password123", null);

        assertFalse(matches);
    }

    @Test
    @DisplayName("密码验证 - 格式错误")
    void testMatches_InvalidFormat() {
        boolean matches = passwordEncoder.matches("password123", "invalidformat");

        assertFalse(matches);
    }
}
