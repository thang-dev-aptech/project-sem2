package com.example.gympro.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class to hash passwords using BCrypt
 * Use this when creating new users or changing passwords
 */
public class PasswordHasher {
    
    /**
     * Hash a plain password using BCrypt
     * @param plainPassword The plain text password
     * @return BCrypt hashed password
     */
    public static String hashPassword(String plainPassword) {
        // BCrypt automatically generates a salt and uses default cost factor (10)
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
    
    /**
     * Verify a plain password against a BCrypt hash
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The BCrypt hash to compare against
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Main method to generate hash for a password
     * Usage: java PasswordHasher <password>
     * Example: java PasswordHasher staff123
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java PasswordHasher <password>");
            System.out.println("Example: java PasswordHasher staff123");
            return;
        }
        
        String password = args[0];
        String hash = hashPassword(password);
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("\nSQL INSERT example:");
        System.out.println("INSERT INTO users (branch_id, username, full_name, email, phone, password_hash, is_active)");
        System.out.println("VALUES (1, 'username', 'Full Name', 'email@example.com', '0123456789', '" + hash + "', 1);");
    }
}

