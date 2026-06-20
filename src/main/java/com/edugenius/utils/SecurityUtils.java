// FILE: src/main/java/com/edugenius/utils/SecurityUtils.java
package com.edugenius.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Security utilities for password hashing and validation
 * Uses SHA-256 for password hashing as per system requirements
 */
public class SecurityUtils {
    
    private static final String HASH_ALGORITHM = "SHA-256";
    
    /**
     * Hash a password using SHA-256
     * @param password Raw password string
     * @return Hashed password as hex string
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(password.getBytes());
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    /**
     * Verify a password against its hash
     * @param password Raw password
     * @param hashedPassword Stored hash
     * @return True if password matches
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        String computedHash = hashPassword(password);
        return computedHash.equals(hashedPassword);
    }
    
    /**
     * Convert byte array to hex string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    /**
     * Generate a random salt (for future use if needed)
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Validate password strength
     * Requirements: minimum 8 characters
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null) return false;
        return password.length() >= 8;
    }
    
    /**
     * Get password strength message
     */
    public static String getPasswordStrengthMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "Password is required";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters";
        }
        return "Password is valid";
    }
}