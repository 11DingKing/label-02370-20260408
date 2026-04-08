package com.library.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("生成Token")
    void testGenerateToken() {
        String token = jwtUtil.generateToken(1L, "testuser", 0);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("解析Token - 获取用户ID")
    void testGetUserId() {
        String token = jwtUtil.generateToken(123L, "testuser", 0);

        Long userId = jwtUtil.getUserId(token);

        assertEquals(123L, userId);
    }

    @Test
    @DisplayName("解析Token - 获取用户名")
    void testGetUsername() {
        String token = jwtUtil.generateToken(1L, "testuser", 0);

        String username = jwtUtil.getUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("解析Token - 获取角色")
    void testGetRole() {
        String token = jwtUtil.generateToken(1L, "testuser", 1);

        Integer role = jwtUtil.getRole(token);

        assertEquals(1, role);
    }

    @Test
    @DisplayName("验证Token - 有效")
    void testValidateToken_Valid() {
        String token = jwtUtil.generateToken(1L, "testuser", 0);

        boolean isValid = jwtUtil.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("验证Token - 无效")
    void testValidateToken_Invalid() {
        boolean isValid = jwtUtil.validateToken("invalid.token.here");

        assertFalse(isValid);
    }

    @Test
    @DisplayName("验证Token - 空Token")
    void testValidateToken_Empty() {
        boolean isValid = jwtUtil.validateToken("");

        assertFalse(isValid);
    }
}
