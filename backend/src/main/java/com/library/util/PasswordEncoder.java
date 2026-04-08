package com.library.util;

import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具
 */
@Component
public class PasswordEncoder {
    
    private static final String SALT_PREFIX = "$SALT$";
    private static final int SALT_LENGTH = 16;
    
    /**
     * 加密密码
     */
    public String encode(String rawPassword) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(rawPassword, salt);
        return SALT_PREFIX + salt + "$" + hashedPassword;
    }
    
    /**
     * 验证密码
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        if (encodedPassword == null || !encodedPassword.startsWith(SALT_PREFIX)) {
            return false;
        }
        String[] parts = encodedPassword.substring(SALT_PREFIX.length()).split("\\$");
        if (parts.length != 2) {
            return false;
        }
        String salt = parts[0];
        String storedHash = parts[1];
        String computedHash = hashPassword(rawPassword, salt);
        return storedHash.equals(computedHash);
    }
    
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
