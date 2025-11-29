package com.example.gympro.utils;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtils {
    
    // Email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Phone regex pattern (Vietnamese format: 10-11 digits, may start with 0 or +84)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(\\+84|0)[0-9]{9,10}$"
    );
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate phone number format (Vietnamese)
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // Remove spaces and dashes
        String cleaned = phone.trim().replaceAll("[\\s-]", "");
        return PHONE_PATTERN.matcher(cleaned).matches();
    }
    
    /**
     * Validate name (non-empty, reasonable length)
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String trimmed = name.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 100;
    }
    
    /**
     * Validate date of birth (not in future, reasonable age)
     */
    public static boolean isValidDateOfBirth(LocalDate dob) {
        if (dob == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate minDate = today.minusYears(120); // Max age 120
        return !dob.isAfter(today) && !dob.isBefore(minDate);
    }
    
    /**
     * Validate price (positive number)
     */
    public static boolean isValidPrice(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty()) {
            return false;
        }
        try {
            double price = Double.parseDouble(priceStr.trim());
            return price > 0 && price <= 1_000_000_000; // Max 1 billion
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate duration in days (positive integer)
     */
    public static boolean isValidDuration(String durationStr) {
        if (durationStr == null || durationStr.trim().isEmpty()) {
            return false;
        }
        try {
            int duration = Integer.parseInt(durationStr.trim());
            return duration > 0 && duration <= 3650; // Max 10 years
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate code format (alphanumeric, dash, underscore, reasonable length)
     */
    public static boolean isValidCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        String trimmed = code.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 50 
            && trimmed.matches("^[A-Za-z0-9_-]+$");
    }
    
    /**
     * Validate date range (start date before end date)
     */
    public static boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        return !startDate.isAfter(endDate);
    }
    
    /**
     * Validate string length
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) {
            return minLength == 0;
        }
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }
}

