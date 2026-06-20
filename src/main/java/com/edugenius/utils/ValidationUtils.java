// FILE: src/main/java/com/edugenius/utils/ValidationUtils.java
package com.edugenius.utils;

import java.util.regex.Pattern;

/**
 * Validation utilities for AAU ID format and form inputs
 * AAU ID formats: UGR/XXXX/XX (Undergraduate) or SGR/XXXX/XX (Postgraduate)
 * Teacher ID format: EMP/XXXX/XX
 */
public class ValidationUtils {
    
    // AAU Student ID Regex: UGR/1234/15 or SGR/1234/15
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^(UGR|SGR)/[0-9]{4}/[0-9]{2}$");
    
    // Teacher Staff ID Regex: EMP/1234/15
    private static final Pattern TEACHER_ID_PATTERN = Pattern.compile("^EMP/[0-9]{4}/[0-9]{2}$");
    
    // Email regex (basic validation)
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    /**
     * Validate AAU Student ID format
     * Example: UGR/1234/15 or SGR/1234/15
     */
    public static boolean isValidStudentId(String aauId) {
        if (aauId == null) return false;
        return STUDENT_ID_PATTERN.matcher(aauId.trim().toUpperCase()).matches();
    }
    
    /**
     * Validate Teacher Staff ID format
     * Example: EMP/1234/15
     */
    public static boolean isValidTeacherId(String staffId) {
        if (staffId == null) return false;
        return TEACHER_ID_PATTERN.matcher(staffId.trim().toUpperCase()).matches();
    }
    
    /**
     * Validate general AAU ID (student or teacher)
     */
    public static boolean isValidAauId(String id) {
        return isValidStudentId(id) || isValidTeacherId(id);
    }
    
    /**
     * Get role from AAU ID
     * Returns "STUDENT" for UGR/SGR, "TEACHER" for EMP
     */
    public static String getRoleFromId(String aauId) {
        if (aauId == null) return null;
        String upperId = aauId.trim().toUpperCase();
        if (upperId.startsWith("UGR") || upperId.startsWith("SGR")) {
            return "STUDENT";
        } else if (upperId.startsWith("EMP")) {
            return "TEACHER";
        }
        return null;
    }
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate full name (not empty, at least 2 characters)
     */
    public static boolean isValidFullName(String fullName) {
        if (fullName == null) return false;
        String trimmed = fullName.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 150;
    }
    
    /**
     * Validate year of study (1-5)
     */
    public static boolean isValidYear(int year) {
        return year >= 1 && year <= 5;
    }
    
    /**
     * Validate semester (1-2)
     */
    public static boolean isValidSemester(int semester) {
        return semester >= 1 && semester <= 2;
    }
    
    /**
     * Get validation error message for AAU ID
     */
    public static String getAauIdErrorMessage(String aauId) {
        if (aauId == null || aauId.trim().isEmpty()) {
            return "AAU ID is required";
        }
        if (isValidStudentId(aauId)) {
            return null; // Valid
        }
        if (isValidTeacherId(aauId)) {
            return null; // Valid
        }
        return "Invalid format. Use: UGR/XXXX/XX, SGR/XXXX/XX, or EMP/XXXX/XX";
    }
    
    /**
     * Get validation error message for full name
     */
    public static String getFullNameErrorMessage(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Full name is required";
        }
        if (fullName.trim().length() < 2) {
            return "Name must be at least 2 characters";
        }
        if (fullName.trim().length() > 150) {
            return "Name is too long (max 150 characters)";
        }
        return null;
    }
}